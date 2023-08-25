package com.example.testapplication

import android.content.Context
import androidx.room.Room
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.testapplication.data.Repository
import com.example.testapplication.data.api.IRemoteSource
import com.example.testapplication.data.api.RemoteSource
import com.example.testapplication.data.local.NotificationDatabase
import com.example.testapplication.data.source.DataRepository
import com.example.testapplication.data.source.DataSource
import com.example.testapplication.util.DefaultViewModelFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module(includes = [DefaultViewModelFactory::class])
@InstallIn(SingletonComponent::class)
object ServiceLocator {
    const val BASE_URL = "https://www.themealdb.com/api/json/"

    @Provides
    @Singleton
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
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    @Provides
    @Singleton
    fun provideDataSource(retrofit: Retrofit): DataSource {
        return RemoteSource(
            retrofit.create(IRemoteSource::class.java)
        )
    }


    @Provides
    @Singleton
    fun provideRepository(
        dataSource: DataSource,
        notificationDatabase: NotificationDatabase,
        dispatcher: DispatcherProvider
    ): DataRepository {
        return Repository(
            dataSource,
            notificationDatabase.notificationDao(),
            notificationDatabase.mealsDao(),
            dispatcher
        )
    }


    @Provides
    @Singleton
    fun provideDispatcher(): DispatcherProvider {
        return DefaultDispatcherProvider()
    }

    @Provides
    @Singleton
    fun provideGlideInstance(@ApplicationContext context: Context) =
        Glide.with(context).setDefaultRequestOptions(
            RequestOptions()
                .placeholder(R.drawable.dummy)
                .error(R.drawable.shiba)
        )
}