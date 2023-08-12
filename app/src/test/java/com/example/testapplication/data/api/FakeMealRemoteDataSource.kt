package com.example.testapplication.data.api

import com.example.testapplication.api.Meals
import com.example.testapplication.api.ResultMeal
import com.example.testapplication.data.Result
import com.example.testapplication.data.source.DataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeMealRemoteDataSource : DataSource {
    private val meals: List<Meals> = listOf(
        Meals(idMeal = 0),
        Meals(idMeal = 1),
        Meals(idMeal = 2),
        Meals(idMeal = 3),
        Meals(idMeal = 4)
    )

    override fun callRandomAPI(): Flow<Result<ResultMeal>> = flow {
        emit(Result.Success(ResultMeal(meals)))
    }

    override fun callDetailApi(id: Int): Flow<Result<ResultMeal>> = flow {
        val oneValue = meals.find { it.idMeal == id }
            ?: throw IndexOutOfBoundsException("Meals Data Does Not Exist")
        val value = ResultMeal(listOf(oneValue))
        emit(Result.Success(value))
    }
}