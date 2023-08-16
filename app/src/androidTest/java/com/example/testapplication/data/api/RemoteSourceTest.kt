package com.example.testapplication.data.api

import androidx.test.filters.MediumTest
import app.cash.turbine.test
import com.example.testapplication.data.model.ResultMeal
import com.example.testapplication.data.source.DataSource
import com.google.gson.GsonBuilder
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@MediumTest
class RemoteSourceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var remoteSource: IRemoteSource
    private lateinit var dataSource: DataSource

    private val fakeMeals = FakeMealsToJson
    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
        .build()
    private val contentType = "application/json".toMediaType()

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        remoteSource = Retrofit.Builder().baseUrl(mockWebServer.url("/"))
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .build()
            .create(IRemoteSource::class.java)
        dataSource = RemoteSource(remoteSource)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun randomMealsApiTest_shouldSuccess() = runTest {
        val shouldReturn = ResultMeal(listOf(fakeMeals.anotherSingleMeals))
        val response = MockResponse()
            .setBody(fakeMeals.anotherSingleToJson())
            .setResponseCode(200)

        mockWebServer.enqueue(response)
        dataSource.callRandomAPI().collect {
            assertThat(it, `is`(shouldReturn))
        }
    }

    @Test
    fun randomMealsApiTest_shouldError() = runTest {
        val response = MockResponse()
            .setBody(fakeMeals.anotherSingleToJson())
            .setResponseCode(400)

        mockWebServer.enqueue(response)
        dataSource.callRandomAPI().test {
            assertThat(awaitError(), instanceOf(HttpException::class.java))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun singleMealsApiTest_shouldSuccess() = runTest {
        val shouldReturn = ResultMeal(listOf(fakeMeals.anotherSingleMeals))
        val response = MockResponse()
            .setBody(fakeMeals.anotherSingleToJson())
            .setResponseCode(200)

        mockWebServer.enqueue(response)
        dataSource.callDetailApi(0).collect {
            assertThat(it, `is`(shouldReturn))
        }
    }

    @Test
    fun singleMealsApiTest_shouldError() = runTest {
        val response = MockResponse()
            .setBody(fakeMeals.anotherSingleToJson())
            .setResponseCode(400)

        mockWebServer.enqueue(response)
        dataSource.callDetailApi(0).test {
            assertThat(awaitError(), instanceOf(HttpException::class.java))
            cancelAndIgnoreRemainingEvents()
        }
    }
}