package com.example.testapplication.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import app.cash.turbine.test
import com.example.testapplication.configuration.MainCoroutineRule
import com.example.testapplication.data.local.MealsDao
import com.example.testapplication.data.local.NotificationDao
import com.example.testapplication.data.local.NotificationDatabase
import com.example.testapplication.data.local.entity.MealsEntity
import com.example.testapplication.data.local.entity.NotificationEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okio.IOException
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.util.Date
import kotlin.jvm.Throws


@OptIn(ExperimentalCoroutinesApi::class)
@MediumTest
@RunWith(AndroidJUnit4::class)
class NotificationDatabaseTest {
    private lateinit var database: NotificationDatabase
    @Mock
    private lateinit var mealsDao: MealsDao
    private lateinit var notificationDao: NotificationDao

    private val dummyMeals: List<MealsEntity> = listOf(
        MealsEntity(id = 0),
        MealsEntity(id = 1),
        MealsEntity(id = 2),
        MealsEntity(id = 3),
        MealsEntity(id = 4)
    )
    private val dummyNotification = mutableListOf(
        NotificationEntity(id = 0, mealId = 0, arrived = Date()),
        NotificationEntity(id = 1, mealId = 1, arrived = Date()),
        NotificationEntity(id = 2, mealId = 2, arrived = Date()),
    )

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun createDB(){
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            NotificationDatabase::class.java
        ).build()
        mealsDao = database.mealsDao()
        notificationDao = database.notificationDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb(){
        database.close()
    }


    @Test
    @Throws(IOException::class)
    fun mealDao_givenListData_saveAll_shouldSuccess() = runTest {
        // When save all list data
        dummyMeals.forEach { mealsDao.saveOneMeal(it) }

        // Then check for first data
        val oneData = mealsDao.findRandom()
        val oneDataWithQuestionMark = dummyMeals.find { it.id == oneData.id }
        assertThat(oneData, equalTo(oneDataWithQuestionMark))
    }


    @Test
    @Throws(IOException::class)
    fun mealDao_givenOneData_saveOne_shouldSuccess() = runTest {
        // Given one data
        val oneData = dummyMeals.first()

        // When save one data
        mealsDao.saveOneMeal(oneData)

        // Then check for that data
        assertThat(oneData, equalTo(mealsDao.findOne(oneData.id)))
    }


    @Ignore("Still wondering why cannot do mock throw exception")
    @Test
    fun mealDao_givenOneData_saveOne_shouldError() = runTest {
        // Given one data
        val oneData = dummyMeals.first()
        mealsDao = mock(MealsDao::class.java)

        // When save one data
        doThrow(IOException::class.java)
            .`when`(mealsDao).saveOneMeal(oneData)

        // Then check
        assertThrows(IOException::class.java){
            mealsDao.saveOneMeal(oneData)
        }

        verify(mealsDao).saveOneMeal(any())
    }

    @Test
    fun notificationDao_givenOneData_saveOne_shouldSuccess() = runTest {
        // Given one data
        val oneData = dummyNotification.find { it.id == 1 }

        // When save one
        notificationDao.saveOneNotification(oneData ?: throw NullPointerException("NOPE"))

        // Then should success
        notificationDao.findAll().test {
            assertThat(oneData, equalTo(awaitItem().first()))
            cancelAndIgnoreRemainingEvents()
        }
    }


    @Test
    fun notificationDao_givenOneData_updateSeenOne_shouldSuccess() = runTest {
        // Given one saved data
        val oneData = dummyNotification.find { it.id == 1 }
        notificationDao.saveOneNotification(oneData!!)

        // When update one data
        assertThat(oneData.isSeen, `is`(false))
        notificationDao.updateNotificationSeen(true)

        // Then check updated data
        notificationDao.findAll().test {
            assertThat(awaitItem().first().isSeen, `is`(true))
            cancelAndIgnoreRemainingEvents()
        }
    }


    @Test
    fun notificationDao_givenOneData_updateIsClicked_shouldSuccess() = runTest {
        // Given one saved data
        val oneData = dummyNotification.find { it.id == 1 }
        notificationDao.saveOneNotification(oneData!!)

        // When update one data
        assertThat(oneData.isClicked, `is`(false))
        assertThat(oneData.mealId, `is`(1))
        notificationDao.updateNotifClickedByMealId(true, 1)

        // Then check updated data
        notificationDao.findAll().test {
            val thatData = awaitItem().first()
            assertThat(thatData.isClicked, `is`(true))
            assertThat(thatData.mealId, `is`(1))
            cancelAndIgnoreRemainingEvents()
        }
    }
}