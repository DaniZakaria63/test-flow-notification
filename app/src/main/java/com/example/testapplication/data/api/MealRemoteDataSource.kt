package com.example.testapplication.data.api

import com.example.testapplication.api.ResultMeal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MealRemoteDataSource(private val remoteApi: RemoteSource) {
    fun callRandomAPI() : Flow<ResultMeal> = flow {
        val randomData = remoteApi.getRandomDish()
        emit(randomData)
    }

    fun callDetailApi(id: Int) : Flow<ResultMeal> = flow {
        val detailData = remoteApi.getDetail(id)
        emit(detailData)
    }
}