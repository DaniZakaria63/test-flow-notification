package com.example.testapplication.ui.list

import androidx.test.espresso.Espresso.onView
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
import com.example.testapplication.configuration.launchFragmentInHiltContainer
import com.example.testapplication.R
import com.example.testapplication.data.Result
import com.example.testapplication.data.source.DataRepository
import com.example.testapplication.data.source.DummyNotificationHelper
import com.example.testapplication.ui.assertion.RecyclerViewItemCount.Companion.withItemCount
import com.example.testapplication.ui.assertion.RecyclerViewItemMatcher
import com.example.testapplication.ui.base.MainViewModel
import com.example.testapplication.ui.main.DefaultMainViewModel
import com.example.testapplication.util.DefaultFragmentFactory
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
import javax.inject.Inject


@HiltAndroidTest
@LargeTest
@UninstallModules(ServiceLocator::class)
class ListFragmentTest {
    private val dummyNotificationHelper = DummyNotificationHelper()
    lateinit var defaultMainViewModel: MainViewModel

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
        defaultMainViewModel = DefaultMainViewModel(repository, DefaultDispatcherProvider())
        launchFragmentInHiltContainer<ListFragment>(
            fragmentFactory = DefaultFragmentFactory(defaultMainViewModel)
        ){
            this@ListFragmentTest.defaultMainViewModel = this._mainViewModel!!
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
                defaultMainViewModel.refreshNotificationList()
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
            defaultMainViewModel.refreshNotificationList()
        }

        preface.await()
        // item view must clicked
        defaultMainViewModel.intentExtra.test {

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
            defaultMainViewModel.updateNotifySeenStatus()
            defaultMainViewModel.refreshNotificationList()
        }

        // using onData to check the model value
        preface.await()
        defaultMainViewModel.allListData.test {
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