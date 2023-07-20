package com.example.testapplication

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Room
import com.example.testapplication.data.Repository
import com.example.testapplication.data.api.MealRemoteDataSource
import com.example.testapplication.data.api.RemoteApi
import com.example.testapplication.data.local.NotificationDao
import com.example.testapplication.data.local.NotificationDatabase
import com.example.testapplication.util.DateConverter

object ServiceLocator {
    private val lock = Any()
    private var database: NotificationDatabase? = null

    @Volatile
    var repository: Repository? = null

    fun provideRepository(context: Context): Repository {
        synchronized(this) {
            return repository ?: createRepository(context)
        }
    }

    @VisibleForTesting
    fun resetRepositoru() {
        database?.apply {
            clearAllTables()
            close()
        }

        synchronized(lock) {
            database = null
            repository = null
        }
    }

    private fun createRepository(context: Context): Repository {
        val newRepo = Repository(
            createMealRemoteDataSource(),
            createNotificationDao(context),
            (context.applicationContext as TestApp).appCoroutine
        )
        repository = newRepo
        return newRepo
    }

    private fun createMealRemoteDataSource() : MealRemoteDataSource {
        return MealRemoteDataSource(RemoteApi.getApi())
    }

    private fun createNotificationDao(context: Context): NotificationDao {
        val database = database ?: createDatabaseInstance(context)
        return database.notificationDao()
    }

    private fun createDatabaseInstance(context: Context): NotificationDatabase {
        val result = Room.databaseBuilder(
            context.applicationContext,
            NotificationDatabase::class.java,
            NotificationDatabase.DB_NAME
        ).addTypeConverter(DateConverter()).build()
        database = result
        return result
    }
}