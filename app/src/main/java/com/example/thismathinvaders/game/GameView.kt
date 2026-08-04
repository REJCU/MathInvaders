package com.example.thismathinvaders.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thismathinvaders.game.data.GameSettings
import com.example.thismathinvaders.game.data.GameStatus
import com.example.thismathinvaders.game.data.GameUiState
import com.example.thismathinvaders.game.data.Meteor
import com.example.thismathinvaders.game.data.Projectile
import com.example.thismathinvaders.game.ui.MathInvadersScreen
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

enum class MathOperation { ADD, SUBTRACT }

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

    fun updateSettings(settings: GameSettings) {
        this.currentSettings = settings
        this.speedMultiplier = baseDifficultySpeed * settings.speedMultiplier

        val nextProblem = generateMathProblem()
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

    private fun getRandomOperation(): MathOperation {
        val activeOps = mutableListOf<MathOperation>()
        if (currentSettings.allowAddition) activeOps.add(MathOperation.ADD)
        if (currentSettings.allowSubtraction) activeOps.add(MathOperation.SUBTRACT)

        if (activeOps.isEmpty()) {
            return MathOperation.ADD
        }
        return activeOps.random()
    }

    private fun generateMathProblem(): Pair<String, Int> {
        val op = getRandomOperation()
        val minVal = currentSettings.minNumberRange.coerceAtLeast(0)
        val maxVal = currentSettings.maxNumberRange.coerceAtLeast(minVal + 5)

        return when (op) {
            MathOperation.ADD -> {
                val a = Random.nextInt(minVal, maxVal + 1)
                val b = Random.nextInt(minVal, maxVal + 1)
                Pair("$a + $b", a + b)
            }
            MathOperation.SUBTRACT -> {
                // a must be strictly greater than minVal so there's room for a b < a
                val a = Random.nextInt(minVal + 1, maxVal + 1)
                val b = Random.nextInt(minVal, a)
                Pair("$a - $b", a - b)
            }
        }
    }


    fun initScreenBounds(width: Float, height: Float) {
        val problem = generateMathProblem()
        if (_uiState.value.screenWidth == 0f) {
            _uiState.update {
                it.copy(
                    screenWidth = width,
                    screenHeight = height,
                    shipX = width / 2f,
                    shipY = height - 200f,
                    targetAnswer = problem.second
                )
            }
            startGameLoop()
        }
    }

    fun startGameLoop() {
        gameLoopJob?.cancel()
        gameLoopJob = viewModelScope.launch {
            var lastTime = System.nanoTime()
            val targetFrameTimeMs = 16L // ~60 FPS
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

            val dx = meteor.x - currentState.shipX
            val dy = newY - currentState.shipY
            val distanceSq = dx * dx + dy * dy
            val collisionThreshold = meteor.radius + shipRadius

            when {
                distanceSq <= collisionThreshold * collisionThreshold -> {
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

                val dx = proj.x - meteor.x
                val dy = proj.y - meteor.y
                val distanceSq = dx * dx + dy * dy
                val collisionRadius = proj.radius + meteor.radius

                if (distanceSq <= collisionRadius * collisionRadius) {
                    projectilesToRemove.add(proj.id)
                    if (proj.value == meteor.answer) {
                        currentScore += 100
                        correctHitsCount++
                        meteorsToRemove.add(meteor)

                        val nextProblem = generateMathProblem()
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
                status = newStatus
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
            val problemPair = generateEquationForTarget(target)
            equation = problemPair.first
            answer = problemPair.second
        } else {
            val problem = generateMathProblem()
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

    private fun generateEquationForTarget(target: Int): Pair<String, Int> {
        val op = getRandomOperation()

        return when (op) {
            MathOperation.ADD -> {
                if (target <= 1) {
                    Pair("$target + 0", target)
                } else {
                    val a = Random.nextInt(1, target)
                    val b = target - a
                    Pair("$a + $b", target)
                }
            }
            MathOperation.SUBTRACT -> {
                val extra = Random.nextInt(1, 10)
                Pair("${target + extra} - $extra", target)
            }
        }
    }

    fun updateShipPosition(x: Float) {
        val width = _uiState.value.screenWidth
        val clampedX = x.coerceIn(shipRadius, (width - shipRadius).coerceAtLeast(shipRadius))
        _uiState.update { it.copy(shipX = clampedX) }
    }

    fun restartGame() {
        val width = _uiState.value.screenWidth
        val height = _uiState.value.screenHeight
        val initialProblem = generateMathProblem()

        correctHitsCount = 0
        incorrectHitsCount = 0
        isGameSessionSaved = false

        _uiState.update {
            GameUiState(
                screenWidth = width,
                screenHeight = height,
                shipX = width / 2f,
                shipY = height - 200f,
                targetAnswer = initialProblem.second
            )
        }
        framesSinceSpawn = 0
        startGameLoop()
    }

    override fun onCleared() {
        gameLoopJob?.cancel()
    }
}