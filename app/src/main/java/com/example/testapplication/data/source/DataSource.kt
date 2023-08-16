package com.example.testapplication.data.source

import com.example.testapplication.data.model.ResultMeal
import kotlinx.coroutines.flow.Flow

interface DataSource {
    fun callRandomAPI(): Flow<ResultMeal>
    fun callDetailApi(id: Int): Flow<ResultMeal>
}