package com.example.testapplication.di

import android.content.Context
import androidx.room.Room
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.example.testapplication.DefaultDispatcherProvider
import com.example.testapplication.DispatcherProvider
import com.example.testapplication.R
import com.example.testapplication.ServiceLocator
import com.example.testapplication.data.Repository
import com.example.testapplication.data.api.IRemoteSource
import com.example.testapplication.data.api.RemoteSource
import com.example.testapplication.data.local.NotificationDatabase
import com.example.testapplication.data.source.DataRepository
import com.example.testapplication.data.source.DataSource
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockWebServer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module(includes = [TestViewModelProvider::class])
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [ServiceLocator::class]
)
class TestServiceLocator {
    @Singleton
    @Provides
    fun provideMockWebServer() : MockWebServer = MockWebServer()


    @Singleton
    @Provides
    fun provideDataSource(mockWebServer: MockWebServer): DataSource {
        val retrofit = Retrofit.Builder().baseUrl(mockWebServer.url("/"))
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .build()
            .create(IRemoteSource::class.java)
        return RemoteSource(retrofit)
    }


    @Provides
    @Singleton
    fun provideNotificationDatabase(@ApplicationContext context: Context): NotificationDatabase {
        return Room.inMemoryDatabaseBuilder(
            context,
            NotificationDatabase::class.java
        ).build()
    }


    @Singleton
    @Provides
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
    fun provideGlideInstance(
        @ApplicationContext context: Context
    ) : RequestManager =
        Glide.with(context).setDefaultRequestOptions(
            RequestOptions()
                .placeholder(R.drawable.dummy)
                .error(R.drawable.shiba)
        )
}