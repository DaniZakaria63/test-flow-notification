package com.example.testapplication.ui.main

import app.cash.turbine.test
import com.example.testapplication.TestCoroutineDispatcher
import com.example.testapplication.data.MainCoroutineRule
import com.example.testapplication.data.Repository
import com.example.testapplication.data.Result
import com.example.testapplication.data.UiState
import com.example.testapplication.data.model.Meals
import com.example.testapplication.data.model.NotificationModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import okio.IOException
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyBoolean
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.verify
import java.util.Date


@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class DefaultMainViewModelTest {
    private val dummyList: MutableList<NotificationModel> = mutableListOf(
        NotificationModel(id = 3, mealId = 1, arrived = Date(), isSeen = true),
        NotificationModel(id = 2, mealId = 1, arrived = Date()),
        NotificationModel(id = 1, mealId = 2, arrived = Date()),
    )

    private lateinit var dispatcher: TestCoroutineDispatcher
    private lateinit var defaultMainViewModel: DefaultMainViewModel

    @Mock
    private lateinit var repository: Repository

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUp() {
        dispatcher = TestCoroutineDispatcher()
        defaultMainViewModel = DefaultMainViewModel(repository, dispatcher)
    }


    @Test
    fun givenListData_saveAllList_shouldSuccess() = runTest {
        // Given dummy list data
        doReturn(flowOf<Result<List<NotificationModel>>>(
            Result.Success(dummyList)
        )).`when`(repository).getAllNotification()

        defaultMainViewModel.refreshNotificationList()
        defaultMainViewModel.allListData.test {
            assertThat(awaitItem(), `is`(UiState(dataList = dummyList)))
            cancelAndIgnoreRemainingEvents()
        }

        verify(repository).getAllNotification()
    }


    @Test
    fun givenError_saveAllList_shouldError() = runTest {
        val throwError = IOException("database closed i guess")
        // Given error response
        doReturn(flowOf<Result<List<NotificationModel>>>(
            Result.Error(throwError)
        )).`when`(repository).getAllNotification()

        defaultMainViewModel.refreshNotificationList()
        defaultMainViewModel.allListData.test {
            assertThat(awaitItem(), `is`(UiState(isError = true)))
            cancelAndIgnoreRemainingEvents()
        }
        verify(repository).getAllNotification()
    }


    @Test
    fun givenLoading_saveAList_shouldLoading() = runTest {
        doReturn(flowOf<Result<List<NotificationModel>>>(
            Result.Loading
        )).`when`(repository).getAllNotification()

        defaultMainViewModel.refreshNotificationList()
        defaultMainViewModel.allListData.test {
            assertThat(awaitItem(), `is`(UiState(isLoading = true)))
            cancelAndIgnoreRemainingEvents()
        }
        verify(repository).getAllNotification()
    }


    @Test
    fun givenNotificationTrigger_saveOneNotification_shouldSuccess() = runTest {
        val oneData = Meals(idMeal = 99)
        val resultData = oneData.asNotificationModel()
        doReturn(flowOf<Result<Meals>>(
            Result.Success(oneData)
        )).`when`(repository).callApiRandomDish()

        doAnswer { args ->
            val data = args.arguments[0] as NotificationModel
            assertThat(data, notNullValue())
            assertThat(data.mealId, equalTo(oneData.idMeal))
        }.`when`(repository).saveLocalNotification(any())

        defaultMainViewModel.notificationTrigger.test {
            defaultMainViewModel.triggerPushOnlineNotify()

            assertThat(awaitItem().mealId, `is`(resultData.mealId))
            cancelAndIgnoreRemainingEvents()
        }
        verify(repository).callApiRandomDish()
        verify(repository).saveLocalNotification(any())
    }


    @Test
    fun givenNotificationTriggerError_saveOneNotification_shouldError() = runTest {
        val throwError = IOException("database closed i guess")

        doReturn(flowOf<Result<Meals>>(
            Result.Error(throwError)
        )).`when`(repository).callApiRandomDish()

        defaultMainViewModel.statusState.test {
            defaultMainViewModel.triggerPushOnlineNotify()
            assertThat(awaitItem(), `is`(Result.Error(throwError)))
            cancelAndIgnoreRemainingEvents()
        }
        verify(repository).callApiRandomDish()
    }


    @Test
    fun givenNotificationTriggerLoading_saveOneNotification_shouldLoading() = runTest {
        doReturn(flowOf<Result<Meals>>(
            Result.Loading
        )).`when`(repository).callApiRandomDish()

        defaultMainViewModel.statusState.test {
            defaultMainViewModel.triggerPushOnlineNotify()
            assertThat(awaitItem(), `is`(Result.Loading))
            cancelAndIgnoreRemainingEvents()
        }
        verify(repository).callApiRandomDish()
    }


    @Test
    fun givenSeenStatus_updateSeenStatus_shouldSuccess() = runTest {
        doAnswer { args ->
            val status : Boolean = args.arguments.get(0) as Boolean
            assertThat(status, `is`(true))
        }.`when`(repository).updateNotifSeenStatus(anyBoolean())

        defaultMainViewModel.updateNotifySeenStatus()

        verify(repository).updateNotifSeenStatus(anyBoolean())
    }


    @Test
    fun givenSeenStatusError_updateSeenStatus_shouldError() = runTest {
        val throwError = IOException("database closed i guess")

        doAnswer { throw throwError }.`when`(repository).updateNotifSeenStatus(anyBoolean())

        defaultMainViewModel.statusState.test {
            defaultMainViewModel.updateNotifySeenStatus()
            assertThat(awaitItem(), `is`(Result.Error(throwError)))
            cancelAndIgnoreRemainingEvents()
        }

        verify(repository).updateNotifSeenStatus(anyBoolean())
    }


    @Test
    fun givenIntentExtra_setParam_shouldSuccess() = runTest {
        val extraKey = "ID"
        val extraValue = 1

        defaultMainViewModel.intentExtra.test {
            defaultMainViewModel.setIntentExtra(extraKey, extraValue)
            assertThat(awaitItem()[extraKey], `is`(extraValue))
            cancelAndIgnoreRemainingEvents()
        }
    }


    @Test
    fun givenActiveList_countTaskActiveBadges_shouldSuccess() = runTest {

        doReturn(flowOf<Result<List<NotificationModel>>>(
            Result.Success(dummyList)
        )).`when`(repository).getAllNotification()

        defaultMainViewModel.refreshNotificationList()
        defaultMainViewModel.badgesCount.test {
            assertThat(awaitItem(), `is`(2)) // just one has been seen
            cancelAndIgnoreRemainingEvents()
        }

        verify(repository).getAllNotification()
    }
}