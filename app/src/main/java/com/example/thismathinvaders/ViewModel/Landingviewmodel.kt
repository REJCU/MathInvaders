package com.example.thismathinvaders.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.thismathinvaders.network.ApodRepository
import com.example.thismathinvaders.repository.ApodResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LandingViewModel(
    private val apodRepository: ApodRepository = ApodRepository()
) : ViewModel() {

    private val _apod = MutableStateFlow<ApodResponse?>(null)
    val apod: StateFlow<ApodResponse?> = _apod.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        fetchApod()
    }

    fun fetchApod() {
        viewModelScope.launch {
            _isLoading.value = true
            _apod.value = apodRepository.getTodaysApod()
            _isLoading.value = false
        }
    }

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LandingViewModel::class.java)) {
                return LandingViewModel() as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
