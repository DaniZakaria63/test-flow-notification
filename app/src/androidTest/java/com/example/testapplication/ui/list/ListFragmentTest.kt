package com.example.testapplication.ui.list

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.filters.LargeTest
import app.cash.turbine.test
import com.example.testapplication.DefaultDispatcherProvider
import com.example.testapplication.ServiceLocator
import com.example.testapplication.configuration.TestCoroutineDispatcher
import com.example.testapplication.configuration.TestFragmentFactory
import com.example.testapplication.configuration.launchFragmentInHiltContainer
import com.example.testapplication.R
import com.example.testapplication.data.Result
import com.example.testapplication.data.source.DataRepository
import com.example.testapplication.data.source.DummyNotificationHelper
import com.example.testapplication.ui.assertion.RecyclerViewItemCount.Companion.withItemCount
import com.example.testapplication.ui.assertion.RecyclerViewItemMatcher
import com.example.testapplication.ui.main.MainViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.Matchers.allOf
import org.hamcrest.core.Is.`is`

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.kotlin.isNull
import java.util.concurrent.CompletableFuture
import javax.inject.Inject


@HiltAndroidTest
@LargeTest
@UninstallModules(ServiceLocator::class)
class ListFragmentTest {
    private val dummyNotificationHelper = DummyNotificationHelper()
    lateinit var mainViewModel: MainViewModel

    @Inject
    @Mock
    lateinit var repository: DataRepository

    @Inject
    lateinit var mockWebServer: MockWebServer

    @get:Rule
    val hiltRule = HiltAndroidRule(this)


    @Before
    fun setUp() {
        hiltRule.inject()
        mainViewModel = MainViewModel(repository, DefaultDispatcherProvider())
        launchFragmentInHiltContainer<ListFragment>(
            fragmentFactory = TestFragmentFactory(mainViewModel)
        ){
            this@ListFragmentTest.mainViewModel = this._mainViewModel!!
        }
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }


    @Test
    fun givenListNotification_checkValue_shouldSuccess() = runTest {
        val dummies = listOf(
            dummyNotificationHelper.getOne(),
            dummyNotificationHelper.getOne(),
            dummyNotificationHelper.getOne()
        )

        // Given data to notification list
        val alltask = async {
            repeat(3){
                repository.saveLocalNotification(dummies[it])
            }
            launch {
                mainViewModel.refreshNotificationList()
            }
        }

        alltask.await()

        repository.getAllNotification().test {
            assertThat((awaitItem() as Result.Success).data.size, `is`(3))
            cancelAndIgnoreRemainingEvents()
        }

        onView(withId(R.id.recycler_view))
            .check(withItemCount(3))

        onView(withId(R.id.recycler_view))
            .perform(RecyclerViewActions.scrollToPosition<ListAdapter.ViewHolder>(1))
            .check(matches(hasDescendant(withText(dummies[1].titleFormatted))))
    }


    @Test
    fun givenListClick_checkIntentExtra_shouldSuccess() = runTest {
        val oneData = dummyNotificationHelper.getOne()

        val preface = async {
            repository.saveLocalNotification(oneData)
            mainViewModel.refreshNotificationList()
        }

        preface.await()
        // item view must clicked
        mainViewModel.intentExtra.test {

            onView(allOf(withId(R.id.recycler_view), isDisplayed()))
                .perform(RecyclerViewActions.actionOnItemAtPosition<ListAdapter.ViewHolder>(
                    0, RecyclerViewItemMatcher(R.id.div_notif)
                ))

            assertThat(awaitItem(), `is`(mapOf("ID" to oneData.mealId)))
            cancelAndIgnoreRemainingEvents()
        }
    }


    @Test
    fun givenListUnseen_checkEffectSeenStatus_shouldSuccess() = runTest {
        val oneData = dummyNotificationHelper.getOne()

        val preface = async {
            repository.saveLocalNotification(oneData)
            mainViewModel.updateNotifySeenStatus()
            mainViewModel.refreshNotificationList()
        }

        // using onData to check the model value
        preface.await()
        mainViewModel.allListData.test {
            assertThat(isNull(), `is`(awaitItem().dataList))
            val item = awaitItem()
            assertThat(item.dataList?.size, `is`(1))
            assertThat(item.dataList!![0].isSeen, `is`(true))
            cancelAndIgnoreRemainingEvents()
        }

        onView(withId(R.id.recycler_view))
            .check(matches(hasDescendant(withText(oneData.titleFormatted))))

    }
}