package com.example.testapplication.data.api

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RemoteApi {
    private const val BASE_URL = "https://www.themealdb.com/api/json/"
    private var apiInstance : IRemoteSource? = null

    fun getApi() : IRemoteSource {
        val logger = HttpLoggingInterceptor{ Log.d("ASD", it   ) }
        logger.level = HttpLoggingInterceptor.Level.BASIC

        val client = OkHttpClient.Builder()
            .addInterceptor(logger)
            .build()

        return apiInstance?:synchronized(RemoteApi::class.java){
            apiInstance = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(IRemoteSource::class.java)
            return apiInstance!!
        }
    }
}