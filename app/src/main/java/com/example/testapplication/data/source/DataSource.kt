package com.example.testapplication.data.source

import com.example.testapplication.api.ResultMeal
import com.example.testapplication.data.Result
import kotlinx.coroutines.flow.Flow

interface DataSource {
    fun callRandomAPI(): Flow<Result<ResultMeal>>
    fun callDetailApi(id: Int): Flow<Result<ResultMeal>>
}