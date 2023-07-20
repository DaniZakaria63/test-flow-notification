package com.example.testapplication.data.api

import com.example.testapplication.data.Result
import retrofit2.http.GET

interface RemoteSource {
    @GET("v1/1/random.php")
    suspend fun getRandomDish() : ResultMeal
}