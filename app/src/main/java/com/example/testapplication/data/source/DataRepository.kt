package com.example.testapplication.data.source

import com.example.testapplication.data.Result
import com.example.testapplication.data.local.entity.MealsEntity
import com.example.testapplication.data.model.Meals
import com.example.testapplication.data.model.NotificationModel
import com.example.testapplication.data.model.ResultMeal
import kotlinx.coroutines.flow.Flow

interface DataRepository {
    fun getDetailMeal(mealsId: Int): Flow<Result<Meals>>
    fun getAllNotification(): Flow<Result<List<NotificationModel>>>
    fun callApiRandomDish(): Flow<Result<Meals>>
    suspend fun getLocalDetailMeal(type: String = "random", id: Int = 0): ResultMeal
    suspend fun getLocalById(id: Int): MealsEntity
    suspend fun getLocalRandom(): MealsEntity
    suspend fun saveLocalMeal(meals: Meals)
    suspend fun saveLocalNotification(notification: NotificationModel)
    suspend fun updateNotifSeenStatus(status: Boolean)
    suspend fun updateNotifClickedStatus(mealsId: Int)
}