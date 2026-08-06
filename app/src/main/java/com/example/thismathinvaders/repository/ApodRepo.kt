package com.example.thismathinvaders.network

import com.example.thismathinvaders.BuildConfig
import com.example.thismathinvaders.repository.ApodResponse

class ApodRepository {
    suspend fun getTodaysApod(): ApodResponse? {
        return try {
            val result = NasaApodApi.service.getApod(apiKey = BuildConfig.NASA_API_KEY)
            // TODO - DO video if time, edge cases aplenty
            if (result.media_type == "image")
                result
            else
                null
        } catch (e: Exception) {
            null
        }
    }
}

