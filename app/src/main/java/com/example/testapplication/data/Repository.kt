package com.example.testapplication.data

import com.example.testapplication.api.Meals
import com.example.testapplication.api.ResultMeal
import com.example.testapplication.data.api.MealRemoteDataSource
import com.example.testapplication.data.local.MealsDao
import com.example.testapplication.data.local.NotificationDao
import com.example.testapplication.data.local.entity.MealsEntity
import com.example.testapplication.data.local.entity.NotificationEntity
import com.example.testapplication.data.source.DataRepository
import com.example.testapplication.data.model.NotificationModel
import com.example.testapplication.data.model.notifDbToModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Repository(
    private val mealRemoteDataSource: MealRemoteDataSource,
    private val notificationDao: NotificationDao,
    private val mealsDao: MealsDao,
    private val appCoroutine: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : DataRepository {
    override fun getDetailMeal(mealsId: Int): Flow<Result<Meals>> {
        return mealRemoteDataSource.callDetailApi(mealsId)
            .catch { emit(getLocalDetailMeal("id", mealsId)) }
            .map { value ->
                (value as Result.Success).let {
                    Result.Success(it.data.getFirst())
                }
            }
    }


    override fun callApiRandomDish(): Flow<Result<Meals>> {
        return mealRemoteDataSource.callRandomAPI()
            .catch { emit(getLocalDetailMeal("random")) }
            .map { value ->
                (value as Result.Success).let {
                    Result.Success(it.data.getFirst())
                }
            }
            .onEach { resultMeal -> saveLocalMeal(resultMeal.data) }
    }


    override fun getAllNotification(): Flow<Result<List<NotificationModel>>> {
        return notificationDao.findAll()
            .map { value -> Result.Success(value.notifDbToModel()) }
    }


    override suspend fun getLocalDetailMeal(type: String, id: Int): Result<ResultMeal> {
        return try {
            val data: MealsEntity =
                if (type.equals("random")) getLocalRandom() else getLocalById(id)
            Result.Success(
                ResultMeal(listOf(data.asDataModel()))
            )
        } catch (e: Exception) {
            Result.Error(e)
        }
    }


    override suspend fun getLocalById(id: Int): MealsEntity =
        withContext(ioDispatcher) {
            return@withContext mealsDao.findOne(id)
        }


    override suspend fun getLocalRandom(): MealsEntity =
        withContext(ioDispatcher) {
            return@withContext mealsDao.findRandom()
        }

    override fun saveLocalMeal(meals: Meals) {
        appCoroutine.launch(ioDispatcher) {
            mealsDao.saveOneMeal(meals.asDatabaseModel())
        }
    }

    override fun saveLocalNotification(notification: NotificationModel) {
        appCoroutine.launch(ioDispatcher) {
            notificationDao.saveOneNotification(notification.asDatabaseModel())
        }
    }
}