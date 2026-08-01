package com.example.thismathinvaders.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_sessions")
data class GameSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val score: Int,
    val difficulty: String,
    val correctHits: Int,
    val incorrectHits: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_stats")
data class UserStatsEntity(
    @PrimaryKey val id: Int = 1,
    val totalGamesPlayed: Int = 0,
    val highScore: Int = 0,
    val totalScore: Long = 0L,
    val totalCorrectHits: Int = 0,
    val totalIncorrectHits: Int = 0
)