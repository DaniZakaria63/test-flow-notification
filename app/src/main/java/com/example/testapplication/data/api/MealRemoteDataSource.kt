package com.example.testapplication.data.api

import com.example.testapplication.api.ResultMeal
import com.example.testapplication.data.Result
import com.example.testapplication.data.source.DataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MealRemoteDataSource(private val remoteApi: RemoteSource) : DataSource {
    override fun callRandomAPI() : Flow<Result<ResultMeal>> = flow {
        val randomData : ResultMeal = remoteApi.getRandomDish()
        emit(Result.Success(randomData))
    }

    override fun callDetailApi(id: Int) : Flow<Result<ResultMeal>> = flow {
        val detailData: ResultMeal = remoteApi.getDetail(id)
        emit(Result.Success(detailData))
    }
}