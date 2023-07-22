package com.example.testapplication.data.api

import com.example.testapplication.api.ResultMeal
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RemoteSource {
    @GET("v1/1/random.php")
    suspend fun getRandomDish() : ResultMeal

    @GET("v1/1/lookup.php")
    suspend fun getDetail(@Query("i") id: Int) : ResultMeal
}