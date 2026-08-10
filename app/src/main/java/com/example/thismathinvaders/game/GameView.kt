package com.example.thismathinvaders.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thismathinvaders.game.data.GameSettings
import com.example.thismathinvaders.game.data.GameStatus
import com.example.thismathinvaders.game.data.GameUiState
import com.example.thismathinvaders.game.data.Meteor
import com.example.thismathinvaders.game.data.Projectile
import com.example.thismathinvaders.game.data.isColliding
import com.example.thismathinvaders.repository.GameRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameViewModel(
    private val repository: GameRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var framesSinceSpawn = 0
    private val spawnEveryFrames = 60
    private val shipRadius = 70f

    private var gameLoopJob: Job? = null

    private var currentDifficulty = "test"
    private var baseDifficultySpeed = 1f
    private var speedMultiplier = 1f
    private var problemDiff = 10

    private var correctHitsCount = 0
    private var incorrectHitsCount = 0
    private var isGameSessionSaved = false

    private var currentSettings = GameSettings()

    private val problemGenerator = MathProblemGenerator()

    fun updateSettings(settings: GameSettings) {
        this.currentSettings = settings
        this.speedMultiplier = baseDifficultySpeed * settings.speedMultiplier

        val nextProblem = problemGenerator.generateProblem(currentSettings)
        _uiState.update { it.copy(targetAnswer = nextProblem.second) }
    }

    fun setDifficulty(difficulty: String) {
        currentDifficulty = difficulty
        baseDifficultySpeed = when (difficulty.lowercase()) {
            "easy" -> 0.6f
            "hard" -> 1.6f
            else -> 1f
        }
        this.speedMultiplier = baseDifficultySpeed * currentSettings.speedMultiplier
    }

    private fun startingLives(): Int {
        return if (currentDifficulty.equals("endless", ignoreCase = true))
            Int.MAX_VALUE else 3
    }

    val shipYPos = 480f
    fun initScreenBounds(width: Float, height: Float) {
        val problem = problemGenerator.generateProblem(currentSettings)
        if (_uiState.value.screenWidth == 0f) {
            _uiState.update {
                it.copy(
                    screenWidth = width,
                    screenHeight = height,
                    shipX = width / 2f,
                    shipY = height - shipYPos,
                    targetAnswer = problem.second,
                    lives = startingLives()
                )
            }
            startGameLoop()
        }
    }

    fun startGameLoop() {
        gameLoopJob?.cancel()
        gameLoopJob = viewModelScope.launch {
            var lastTime = System.nanoTime()
            val targetFrameTimeMs = 16L // 60 FPS
            while (isActive) {
                val currentTime = System.nanoTime()
                val deltaTime = (currentTime - lastTime) / 1_000_000_000f
                lastTime = currentTime
                updateGameLogic(deltaTime)
                delay(targetFrameTimeMs)
            }
        }
    }

    private fun saveGameStats(finalScore: Int) {
        viewModelScope.launch {
            repository.recordFinishedGame(
                score = finalScore,
                difficulty = currentDifficulty,
                correctHits = correctHitsCount,
                incorrectHits = incorrectHitsCount
            )
        }
    }

    private fun updateGameLogic(deltaTime: Float) {
        val currentState = _uiState.value
        if (currentState.status != GameStatus.PLAYING) return

        framesSinceSpawn++

        val updatedProjectiles = currentState.projectiles.mapNotNull { proj ->
            val newY = proj.y - (proj.speed * deltaTime)
            if (newY < 0f) null else proj.copy(y = newY)
        }.toMutableList()

        val remainingMeteors = mutableListOf<Meteor>()
        var currentLives = currentState.lives

        for (meteor in currentState.meteors) {
            val newY = meteor.y + meteor.speed * speedMultiplier * (deltaTime * 60f)

            when {
                isColliding(meteor.x, newY, meteor.radius, currentState.shipX, currentState.shipY, shipRadius) -> {
                    currentLives -= 1
                }
                newY - meteor.radius > currentState.screenHeight -> { }
                else -> {
                    remainingMeteors.add(meteor.copy(y = newY))
                }
            }
        }

        var currentScore = currentState.score
        var newTargetAnswer = currentState.targetAnswer
        val projectilesToRemove = mutableSetOf<Long>()
        val meteorsToRemove = mutableSetOf<Meteor>()

        for (proj in updatedProjectiles) {
            for (meteor in remainingMeteors) {
                if (meteorsToRemove.contains(meteor)) continue

                if (isColliding(proj.x, proj.y, proj.radius, meteor.x, meteor.y, meteor.radius)) {
                    projectilesToRemove.add(proj.id)
                    if (proj.value == meteor.answer) {
                        currentScore += 100
                        correctHitsCount++
                        meteorsToRemove.add(meteor)

                        val nextProblem = problemGenerator.generateProblem(currentSettings)
                        newTargetAnswer = nextProblem.second
                    } else {
                        currentScore = (currentScore - 50).coerceAtLeast(0)
                        incorrectHitsCount++
                    }
                    break
                }
            }
        }

        updatedProjectiles.removeAll { it.id in projectilesToRemove }
        remainingMeteors.removeAll(meteorsToRemove)

        if (framesSinceSpawn >= spawnEveryFrames) {
            spawnMeteor(remainingMeteors, currentState.screenWidth)
            framesSinceSpawn = 0
        }

        val newStatus = if (currentLives <= 0) GameStatus.GAME_OVER else GameStatus.PLAYING

        if (newStatus == GameStatus.GAME_OVER && !isGameSessionSaved) {
            isGameSessionSaved = true
            saveGameStats(currentScore)
        }

        _uiState.update {
            it.copy(
                meteors = remainingMeteors,
                projectiles = updatedProjectiles,
                lives = currentLives,
                score = currentScore,
                targetAnswer = newTargetAnswer,
                status = newStatus,
                correctHits = correctHitsCount,
                incorrectHits = incorrectHitsCount
            )
        }
    }

    fun fireProjectile() {
        val currentState = _uiState.value
        if (currentState.status != GameStatus.PLAYING) return

        val newProjectile = Projectile(
            id = System.nanoTime(),
            x = currentState.shipX,
            y = currentState.shipY - 70f,
            value = currentState.targetAnswer,
            speed = 1400f,
            radius = 12f,
        )

        _uiState.update { it.copy(projectiles = it.projectiles + newProjectile) }
    }

    private fun spawnMeteor(list: MutableList<Meteor>, width: Float) {
        if (width <= 0f) return
        val padding = 80f
        val spawnX = Random.nextFloat() * (width - padding * 2) + padding

        val shouldMatchTarget = Random.nextBoolean()
        val equation: String
        val answer: Int

        if (shouldMatchTarget) {
            val target = _uiState.value.targetAnswer
            val problemPair = problemGenerator.generateEquationForTarget(target, currentSettings)
            equation = problemPair.first
            answer = problemPair.second
        } else {
            val problem = problemGenerator.generateProblem(currentSettings)
            equation = problem.first
            answer = problem.second
        }

        val baseSpeed = 3f + Random.nextFloat() * 2f

        list.add(
            Meteor(
                x = spawnX,
                y = -80f,
                equation = equation,
                answer = answer,
                speed = baseSpeed
            )
        )
    }

    fun updateShipPosition(x: Float) {
        val width = _uiState.value.screenWidth
        val clampedX = x.coerceIn(shipRadius, (width - shipRadius).coerceAtLeast(shipRadius))
        _uiState.update { it.copy(shipX = clampedX) }
    }

    fun restartGame() {
        val width = _uiState.value.screenWidth
        val height = _uiState.value.screenHeight
        val initialProblem = problemGenerator.generateProblem(currentSettings)

        correctHitsCount = 0
        incorrectHitsCount = 0
        isGameSessionSaved = false

        _uiState.update {
            GameUiState(
                screenWidth = width,
                screenHeight = height,
                shipX = width / 2f,
                shipY = height - 200f,
                targetAnswer = initialProblem.second,
                lives = startingLives()

            )
        }
        framesSinceSpawn = 0
        startGameLoop()
    }

    override fun onCleared() {
        gameLoopJob?.cancel()
    }
}