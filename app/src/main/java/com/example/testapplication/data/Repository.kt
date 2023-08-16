package com.example.testapplication.data

import com.example.testapplication.DefaultDispatcherProvider
import com.example.testapplication.DispatcherProvider
import com.example.testapplication.data.local.MealsDao
import com.example.testapplication.data.local.NotificationDao
import com.example.testapplication.data.local.entity.MealsEntity
import com.example.testapplication.data.model.Meals
import com.example.testapplication.data.model.NotificationModel
import com.example.testapplication.data.model.ResultMeal
import com.example.testapplication.data.model.notifDbToModel
import com.example.testapplication.data.source.DataRepository
import com.example.testapplication.data.source.DataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

@OptIn(ExperimentalCoroutinesApi::class)
class Repository(
    private val mealRemoteDataSource: DataSource,
    private val notificationDao: NotificationDao,
    private val mealsDao: MealsDao,
    private val dispatcher: DispatcherProvider = DefaultDispatcherProvider()
) : DataRepository {

    override fun getDetailMeal(mealsId: Int): Flow<Result<Meals>> {
        return mealRemoteDataSource.callDetailApi(mealsId)
            .flowOn(dispatcher.io)
            .onStart { Result.Loading }
            .retry(2)
            .catch { emit(getLocalDetailMeal("id", mealsId)) }
            .map { value -> Result.Success(value.getFirst()) }
    }


    override fun callApiRandomDish(): Flow<Result<Meals>> {
        return mealRemoteDataSource.callRandomAPI()
            .flowOn(dispatcher.io)
            .onStart { Result.Loading }
            .retry(2)
            .catch { emit(getLocalDetailMeal("random")) }
            .map { value -> Result.Success(value.getFirst()) }
            .onEach { value -> saveLocalMeal(value.data) }
    }


    override fun getAllNotification(): Flow<Result<List<NotificationModel>>> =
        runBlocking(dispatcher.io) {
            async {
                notificationDao.findAll()
                    .onStart { Result.Loading }
                    .mapLatest { value -> Result.Success(value.notifDbToModel()) }
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

    override suspend fun updateNotifSeenStatus(status: Boolean) {
        coroutineScope {
            launch(dispatcher.io) {
                notificationDao.updateNotificationSeen(status)
            }
        }
    }

    override suspend fun updateNotifClickedStatus(mealsId: Int) {
        coroutineScope {
            launch(dispatcher.io) {
                notificationDao.updateNotifClickedByMealId(true, mealsId)
            }
        }
    }


}