package com.example.testapplication.data

import com.example.testapplication.data.api.FakeMealRemoteDataSource
import com.example.testapplication.data.local.FakeMealsDao
import com.example.testapplication.data.local.FakeNotificationDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Test

class RepositoryTest {
    private lateinit var repository: Repository

    @ExperimentalCoroutinesApi
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUp() {
        val fakeRemoteSource = FakeMealRemoteDataSource()
        val fakeMealsDao = FakeMealsDao()
        val fakeNotificationDao = FakeNotificationDao()
        repository = Repository(
            fakeRemoteSource,
            fakeNotificationDao,
            fakeMealsDao,
            Dispatchers.Main
        )
    }

    @After
    fun tearDown() {
    }


}