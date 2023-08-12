package com.example.testapplication

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Room
import com.example.testapplication.data.Repository
import com.example.testapplication.data.api.RemoteSource
import com.example.testapplication.data.api.RemoteApi
import com.example.testapplication.data.local.MealsDao
import com.example.testapplication.data.local.NotificationDao
import com.example.testapplication.data.local.NotificationDatabase
import com.example.testapplication.data.source.DataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object ServiceLocator {


    @Provides
    fun provideNotificationDatabase(@ApplicationContext context: Context): NotificationDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            NotificationDatabase::class.java,
            NotificationDatabase.DB_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }


    @Provides
    @Singleton
    fun provideRepository(notificationDatabase: NotificationDatabase): DataRepository {
        return Repository(
            RemoteSource(RemoteApi.getApi()),
            notificationDatabase.notificationDao(),
            notificationDatabase.mealsDao(),
        )
    }


    @Provides
    @Singleton
    fun provideDispatcher() : DispatcherProvider {
        return DefaultDispatcherProvider()
    }
}