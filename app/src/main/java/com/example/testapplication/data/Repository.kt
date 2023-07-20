package com.example.testapplication.data

import com.example.testapplication.api.Meals
import com.example.testapplication.api.ResultMeal
import com.example.testapplication.data.Result
import com.example.testapplication.data.api.MealRemoteDataSource
import com.example.testapplication.data.local.NotificationDao
import com.example.testapplication.data.model.NotificationModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class Repository(
    private val mealRemoteDataSource: MealRemoteDataSource,
    private val notificationDao: NotificationDao,
    private val coroutineScope: CoroutineScope
) {
    fun callApiRandomDish() : Flow<Result<Meals>> {
        return mealRemoteDataSource.callRandomAPI()
            .map { value: ResultMeal -> Result.Success(value.getFirst()) }
            .onEach { resultMeal -> saveLocalMeal(resultMeal.data)}
    }

    private fun saveLocalMeal(meals: Meals){
        coroutineScope.launch {
            notificationDao.saveOneMeal(meals.asDatabaseModel())
        }
    }

    fun saveLocalNotification(notification: NotificationModel){
        coroutineScope.launch {
            notificationDao.saveOneNotification(notification.asDatabaseModel())
        }
    }
}