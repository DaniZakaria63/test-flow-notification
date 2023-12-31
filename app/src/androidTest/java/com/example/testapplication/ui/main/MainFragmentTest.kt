package com.example.testapplication.ui.main

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingPolicies
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import app.cash.turbine.test
import com.example.testapplication.R
import com.example.testapplication.ServiceLocator
import com.example.testapplication.espresso.EspressoIdlingResource
import com.example.testapplication.configuration.TestCoroutineDispatcher
import com.example.testapplication.di.launchFragmentInHiltContainer
import com.example.testapplication.data.Result
import com.example.testapplication.data.api.FakeMealsToJson
import com.example.testapplication.data.source.DataRepository
import com.example.testapplication.util.DefaultFragmentFactory
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@LargeTest
@UninstallModules(ServiceLocator::class)
@HiltAndroidTest
class MainFragmentTest {
    private val fakeMeals = FakeMealsToJson
    private lateinit var context: Context

    lateinit var defaultMainViewModel: DefaultMainViewModel

    @Inject @Mock
    lateinit var repository: DataRepository

    @Inject
    lateinit var mockWebServer: MockWebServer

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.idlingResource)
        IdlingPolicies.setIdlingResourceTimeout(1, TimeUnit.MINUTES)
        context = InstrumentationRegistry.getInstrumentation().targetContext

        hiltRule.inject()
        defaultMainViewModel = DefaultMainViewModel(repository, TestCoroutineDispatcher())
        launchFragmentInHiltContainer<MainFragment>(fragmentFactory = DefaultFragmentFactory(defaultMainViewModel))
    }


    @After
    fun tearDown() {
        mockWebServer.shutdown()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.idlingResource)
    }


    @Test
    fun triggerOfflineNotification_checkStateViewModel_shouldSuccess() = runTest {

        defaultMainViewModel.notificationTrigger.test {
            onView(withId(R.id.button)).perform(click())

            assertThat(awaitItem(), notNullValue())
            cancelAndIgnoreRemainingEvents()
        }

        repository.getAllNotification().test {
            assertThat((awaitItem() as Result.Success).data.size, `is`(1))
            cancelAndIgnoreRemainingEvents()
        }
    }


    @Test
    fun triggerOnlineNotification_checkStateViewModel_shouldSuccess() = runTest {
        val response = MockResponse()
            .setBody(fakeMeals.anotherSingleToJson())
            .setResponseCode(200)
        mockWebServer.enqueue(response)

        onView(withId(R.id.button2)).perform(click())

        repository.getAllNotification().test {
            assertThat(awaitItem(), instanceOf(Result.Success::class.java))
            assertThat((awaitItem() as Result.Success).data.count(), `is`(1))
            cancelAndIgnoreRemainingEvents()
        }
    }


    @Test
    fun triggerOnlineNotificationError_checkStateViewModel_shouldError() = runTest {
        val response = MockResponse()
            .setBody(fakeMeals.anotherSingleToJson())
            .setResponseCode(400)
        mockWebServer.enqueue(response)

        onView(withId(R.id.button2)).perform(click())

        repository.getAllNotification().test {
            assertThat((awaitItem() as Result.Success).data.count(), `is`(0))
            cancelAndIgnoreRemainingEvents()
        }
    }
}