package com.example.thismathinvaders.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thismathinvaders.game.data.GameStatus
import com.example.thismathinvaders.game.data.GameUiState
import com.example.thismathinvaders.game.data.Meteor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var framesSinceSpawn = 0
    private val spawnEveryFrames = 60
    private val shipRadius = 70f

    private var gameLoopJob: Job? = null

    private var speedMultiplier = 1f
    private var problemDiff = 10

    fun initScreenBounds(width: Float, height: Float) {
        if (_uiState.value.screenWidth == 0f) {
            _uiState.update {
                it.copy(
                    screenWidth = width,
                    screenHeight = height,
                    shipX = width / 2f,
                    shipY = height - 200f
                )
            }
            startGameLoop()
        }
    }

    fun startGameLoop() {
        // cancels previous loop before starting new one so never stack.
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

    private fun updateGameLogic(deltaTime: Float) {
        val currentState = _uiState.value
        if (currentState.status != GameStatus.PLAYING) return

        framesSinceSpawn++

        val updatedMeteors = mutableListOf<Meteor>()
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
                newY - meteor.radius > currentState.screenHeight -> {
                    // falls off the bottom
                }
                else -> {
                    updatedMeteors.add(meteor.copy(y = newY))
                }
            }
        }

        if (framesSinceSpawn >= spawnEveryFrames) {
            spawnMeteor(updatedMeteors, currentState.screenWidth)
            framesSinceSpawn = 0
        }

        val newStatus = if (currentLives <= 0) GameStatus.GAME_OVER else GameStatus.PLAYING

        _uiState.update {
            it.copy(
                meteors = updatedMeteors,
                lives = currentLives,
                status = newStatus
            )
        }
    }

    fun setDifficulty(difficulty: String) {
        speedMultiplier = when (difficulty) {
            "easy" -> 0.6f
            "hard" -> 1.6f
            else -> 1f
        }
        problemDiff = when (difficulty) {
            "easy" -> 5
            "hard" -> 20
            else -> 10
        }
    }

    private fun spawnMeteor(list: MutableList<Meteor>, width: Float) {
        if (width <= 0f) return
        val padding = 80f
        val a = Random.nextInt(1, problemDiff)
        val b = Random.nextInt(1, problemDiff)
        val spawnX = Random.nextFloat() * (width - padding * 2) + padding
        list.add(
            Meteor(
                x = spawnX,
                y = -80f,
                equation = "$a + $b",
                answer = a + b,
                speed = 3f + Random.nextFloat() * 2f
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
        _uiState.update {
            GameUiState(
                screenWidth = width,
                screenHeight = height,
                shipX = width / 2f,
                shipY = height - 200f
            )
        }
        framesSinceSpawn = 0
        startGameLoop()
    }

    override fun onCleared() {
        super.onCleared()
        gameLoopJob?.cancel()
    }
}