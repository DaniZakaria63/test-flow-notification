package com.example.testapplication.data

import com.example.testapplication.api.Meals
import com.example.testapplication.api.ResultMeal
import com.example.testapplication.data.Result
import com.example.testapplication.data.api.MealRemoteDataSource
import com.example.testapplication.data.local.MealsDao
import com.example.testapplication.data.local.NotificationDao
import com.example.testapplication.data.model.NotificationModel
import com.example.testapplication.data.model.notifDbToModel
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
    private val mealsDao: MealsDao,
    private val coroutineScope: CoroutineScope
) {
    fun getDetailMeal(mealsId: Int) : Flow<Result<Meals>>{
        return mealRemoteDataSource.callDetailApi(mealsId)
            .map { value -> Result.Success(value.getFirst()) }
    }

    fun getAllNotification() : Flow<Result<List<NotificationModel>>> {
        return notificationDao.findAll()
            .map { value ->  Result.Success(value.notifDbToModel())}
    }

    fun callApiRandomDish() : Flow<Result<Meals>> {
        return mealRemoteDataSource.callRandomAPI()
            .map { value: ResultMeal -> Result.Success(value.getFirst()) }
            .onEach { resultMeal -> saveLocalMeal(resultMeal.data)}
    }

    private fun saveLocalMeal(meals: Meals){
        coroutineScope.launch {
            mealsDao.saveOneMeal(meals.asDatabaseModel())
        }
    }

    fun saveLocalNotification(notification: NotificationModel){
        coroutineScope.launch {
            notificationDao.saveOneNotification(notification.asDatabaseModel())
        }
    }
}