package com.example.testapplication.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.testapplication.data.local.FakeMealsDao
import com.example.testapplication.data.local.entity.MealsEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class FakeMealsTest {
    private lateinit var fakeMealsDao: FakeMealsDao
    private val meals = mutableListOf(
        MealsEntity(id = 0),
        MealsEntity(id = 1),
        MealsEntity(id = 2),
        MealsEntity(id = 3),
        MealsEntity(id = 4),
    )

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        fakeMealsDao = FakeMealsDao()
        runBlocking{
            meals.forEach { fakeMealsDao.saveOneMeal(it) }
        }
    }

    @Test
    fun `show all no data return empty`() {
        val allData = fakeMealsDao.findAll()

        assertThat(allData).isNotEmpty()
    }

    @Test
    fun `show all with dummy data return all`() = runTest {
        val allData = fakeMealsDao.findAll()

        assertThat(allData).isEqualTo(meals)
    }

    @Test
    fun `show one with random data return one`(){
        val oneData = fakeMealsDao.findRandom()

        assertThat(oneData).isNotNull()
    }

    @Test
    fun `show one with id from item list`(){
        fakeMealsDao.saveOneMeal(meals.get(1))
        val oneData = meals[1]

        val givenData = fakeMealsDao.findOne(oneData.id)

        assertThat(givenData).isEqualTo(oneData)
    }

    @Test
    fun `show one random from list to meals`(){
        fakeMealsDao.saveOneMeal(meals.get(3))

        val oneData = fakeMealsDao.findRandom()
        val givenData = meals.find { it.id == oneData.id}

        assertThat(givenData).isNotNull()
        assertThat(givenData).isEqualTo(oneData)
    }
}