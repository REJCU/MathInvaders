package com.example.thismathinvaders.network

import com.example.thismathinvaders.repository.NasaApodService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.jvm.java

object NasaApodApi {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.nasa.gov/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: NasaApodService = retrofit.create(NasaApodService::class.java)
}
