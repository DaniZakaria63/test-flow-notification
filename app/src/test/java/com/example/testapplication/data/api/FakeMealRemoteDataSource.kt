package com.example.testapplication.data.api

import com.example.testapplication.api.Meals
import com.example.testapplication.api.ResultMeal
import com.example.testapplication.data.Result
import com.example.testapplication.data.source.DataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeMealRemoteDataSource(private val meals: List<Meals>) : DataSource {
    override fun callRandomAPI(): Flow<ResultMeal> = flow {
        emit(ResultMeal(meals))
    }

    override fun callDetailApi(id: Int): Flow<ResultMeal> = flow {
        val oneValue = meals.find { it.idMeal == id }
            ?: throw IndexOutOfBoundsException("Meals Data Does Not Exist")
        val value = ResultMeal(listOf(oneValue))
        emit(value)
    }
}