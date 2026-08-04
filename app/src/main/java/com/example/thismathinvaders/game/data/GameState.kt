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
    // TODO - relevant answer for each diffuculty
    val targetAnswer: Int = 0,
    val score: Int = 0,
    val lives: Int = 3,
    val correctHits: Int = 0,
    val incorrectHits: Int = 0,
    val shipX: Float = 0f,
    val shipY: Float = 0f,
    val meteors: List<Meteor> = emptyList(),
    val projectiles: List<Projectile> = emptyList(),
    val screenWidth: Float = 0f,
    val screenHeight: Float = 0f,
    @DrawableRes val shipDrawableRes: Int = R.drawable.spaceship_svgrepo,
    @DrawableRes val meteorDrawableRes: Int = R.drawable.shooting_star_svgrepo,
    @DrawableRes val projectileDrawableRes: Int = R.drawable.rocket_ship_launch_missile_svgrepo
)

data class Projectile(
    val id: Long = System.currentTimeMillis(),
    val x: Float,
    val y: Float,
    val value: Int,
    val radius: Float = 12f,
    val speed: Float = 1400f
)

data class GameSettings(
    val allowAddition: Boolean = true,
    val allowSubtraction: Boolean = false,

    val minNumberRange: Int = 1,
    val maxNumberRange: Int = 10,

    val speedMultiplier: Float = 1f
)