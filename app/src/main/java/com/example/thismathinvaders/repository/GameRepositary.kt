package com.example.thismathinvaders.repository

import com.example.thismathinvaders.repository.GameDao
import com.example.thismathinvaders.data.local.entity.GameSessionEntity
import com.example.thismathinvaders.data.local.entity.UserStatsEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class GameRepository(private val gameDao: GameDao) {

    val topScores: Flow<List<GameSessionEntity>> = gameDao.getTopScores()
    val userStats: Flow<UserStatsEntity?> = gameDao.getUserStats()

    suspend fun recordFinishedGame(
        score: Int,
        difficulty: String,
        correctHits: Int,
        incorrectHits: Int
    ) {
        val session = GameSessionEntity(
            score = score,
            difficulty = difficulty,
            correctHits = correctHits,
            incorrectHits = incorrectHits
        )
        val currentStats = gameDao.getUserStats().firstOrNull()
        gameDao.saveGameResult(session, currentStats)
    }
}