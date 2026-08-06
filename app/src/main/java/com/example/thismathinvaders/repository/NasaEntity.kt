package com.example.thismathinvaders.repository

import retrofit2.http.GET
import retrofit2.http.Query

data class ApodResponse(
    val date: String,
    val explanation: String,
    val title: String,
    val url: String,
    val hdurl: String? = null,
    val media_type: String
)

interface NasaApodService {
    @GET("planetary/apod")
    suspend fun getApod(
        @Query("api_key") apiKey: String
    ): ApodResponse
}

