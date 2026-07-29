package com.example.thismathinvaders.game.data

import androidx.annotation.DrawableRes
import com.example.thismathinvaders.R

enum class GameStatus {
    PLAYING,
    GAME_OVER
}

data class Meteor(
    val id: Long = System.currentTimeMillis() + (0..10000).random(),
    var x: Float,
    var y: Float,
    val equation: String,
    val answer: Int,
    val radius: Float = 70f,
    var speed: Float
)

data class GameUiState(
    val status: GameStatus = GameStatus.PLAYING,
    val score: Int = 0,
    val lives: Int = 3,
    val shipX: Float = 0f,
    val shipY: Float = 0f,
    val meteors: List<Meteor> = emptyList(),
    val screenWidth: Float = 0f,
    val screenHeight: Float = 0f,
    @DrawableRes val shipDrawableRes: Int = R.drawable.spaceship_svgrepo,
    @DrawableRes val meteorDrawableRes: Int = R.drawable.shooting_star_svgrepo
)
