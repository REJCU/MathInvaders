package com.example.thismathinvaders.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.thismathinvaders.data.local.entity.GameSessionEntity
import com.example.thismathinvaders.data.local.entity.UserStatsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: GameSessionEntity): Long

    @Query("SELECT * FROM game_sessions ORDER BY score DESC LIMIT 10")
    fun getTopScores(): Flow<List<GameSessionEntity>>

    @Query("SELECT * FROM game_sessions ORDER BY timestamp DESC LIMIT 20")
    fun getRecentSessions(): Flow<List<GameSessionEntity>>

    @Query("SELECT MAX(score) FROM game_sessions WHERE difficulty = :difficulty")
    fun getHighScoreForDifficulty(difficulty: String): Flow<Int?>

    @Query("SELECT * FROM user_stats WHERE id = 1")
    fun getUserStats(): Flow<UserStatsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUserStats(stats: UserStatsEntity)

    @Transaction
    suspend fun saveGameResult(session: GameSessionEntity, currentStats: UserStatsEntity?) {
        insertSession(session)

        val stats = currentStats ?: UserStatsEntity()
        val updatedStats = stats.copy(
            totalGamesPlayed = stats.totalGamesPlayed + 1,
            highScore = maxOf(stats.highScore, session.score),
            totalScore = stats.totalScore + session.score,
            totalCorrectHits = stats.totalCorrectHits + session.correctHits,
            totalIncorrectHits = stats.totalIncorrectHits + session.incorrectHits
        )
        upsertUserStats(updatedStats)
    }
}