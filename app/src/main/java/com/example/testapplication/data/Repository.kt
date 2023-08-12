package com.example.testapplication.data

import com.example.testapplication.DefaultDispatcherProvider
import com.example.testapplication.DispatcherProvider
import com.example.testapplication.api.Meals
import com.example.testapplication.api.ResultMeal
import com.example.testapplication.data.local.MealsDao
import com.example.testapplication.data.local.NotificationDao
import com.example.testapplication.data.local.entity.MealsEntity
import com.example.testapplication.data.source.DataRepository
import com.example.testapplication.data.model.NotificationModel
import com.example.testapplication.data.model.notifDbToModel
import com.example.testapplication.data.source.DataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class Repository(
    private val mealRemoteDataSource: DataSource,
    private val notificationDao: NotificationDao,
    private val mealsDao: MealsDao,
    private val dispatcher: DispatcherProvider = DefaultDispatcherProvider()
) : DataRepository {

    override fun getDetailMeal(mealsId: Int): Flow<Result<Meals>> {
        return mealRemoteDataSource.callDetailApi(mealsId)
            .onStart { Result.Loading }
            .catch { emit(getLocalDetailMeal("id", mealsId)) }
            .map { value -> Result.Success(value.getFirst()) }
    }


    override fun callApiRandomDish(): Flow<Result<Meals>> {
        return mealRemoteDataSource.callRandomAPI()
            .onStart { Result.Loading }
            .catch { emit(getLocalDetailMeal("random")) }
            .map { value -> Result.Success(value.getFirst()) }
            .onEach { value -> saveLocalMeal(value.data) }
    }


    override fun getAllNotification(): Flow<Result<List<NotificationModel>>> =
        runBlocking(dispatcher.default) {
            async(dispatcher.io) {
                notificationDao.findAll()
                    .onStart { Result.Loading }
                    .map { value -> Result.Success(value.notifDbToModel()) }
                    .catch { Result.Error(it) }
            }.await()
        }


    override suspend fun getLocalDetailMeal(type: String, id: Int): ResultMeal {
        val result = if (type == "random") getLocalRandom() else getLocalById(id)
        return ResultMeal(listOf(result.asDataModel()))
    }


    override suspend fun getLocalById(id: Int): MealsEntity = withContext(dispatcher.main) {
        async (dispatcher.io){
            mealsDao.findOne(id)
        }.await()
    }


    override suspend fun getLocalRandom(): MealsEntity = withContext(dispatcher.main) {
        async(dispatcher.io){
            mealsDao.findRandom()
        }.await()
    }


    override suspend fun saveLocalMeal(meals: Meals) {
        coroutineScope {
            launch(dispatcher.io) {
                mealsDao.saveOneMeal(meals.asDatabaseModel())
            }
        }
    }


    override suspend fun saveLocalNotification(notification: NotificationModel) {
        coroutineScope {
            launch(dispatcher.io) {
                notificationDao.saveOneNotification(notification.asDatabaseModel())
            }
        }
    }
}