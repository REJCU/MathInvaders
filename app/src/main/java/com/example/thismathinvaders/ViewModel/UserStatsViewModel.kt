package com.example.thismathinvaders.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.thismathinvaders.data.local.entity.GameSessionEntity
import com.example.thismathinvaders.data.local.entity.UserStatsEntity
import com.example.thismathinvaders.repository.GameRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class UserStatsViewModel(
    private val repository: GameRepository
) : ViewModel() {

    val topScores: StateFlow<List<GameSessionEntity>> = repository.topScores
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val userStats: StateFlow<UserStatsEntity?> = repository.userStats
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    class Factory(private val repository: GameRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UserStatsViewModel::class.java)) {
                return UserStatsViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}