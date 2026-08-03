package com.example.thismathinvaders.ViewModel

import androidx.lifecycle.ViewModel
import com.example.thismathinvaders.game.data.GameSettings
import com.example.thismathinvaders.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SettingsViewModel(
    private val repository: GameRepository
) : ViewModel() {

    private val _settings = MutableStateFlow(GameSettings())
    val settings: StateFlow<GameSettings> = _settings.asStateFlow()

    fun updateSettings(newSettings: GameSettings) {
        _settings.update { newSettings }
    }
}