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
import com.example.testapplication.configuration.ExpressoIdlingResource
import com.example.testapplication.configuration.TestCoroutineDispatcher
import com.example.testapplication.configuration.TestFragmentFactory
import com.example.testapplication.configuration.launchFragmentInHiltContainer
import com.example.testapplication.data.Result
import com.example.testapplication.data.api.FakeMealsToJson
import com.example.testapplication.data.source.DataRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

    lateinit var mainViewModel: MainViewModel

    @Inject @Mock
    lateinit var repository: DataRepository

    @Inject
    lateinit var mockWebServer: MockWebServer

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(ExpressoIdlingResource.idlingResource)
        IdlingPolicies.setIdlingResourceTimeout(1, TimeUnit.MINUTES)
        context = InstrumentationRegistry.getInstrumentation().targetContext

        hiltRule.inject()
        mainViewModel = MainViewModel(repository, TestCoroutineDispatcher())
        launchFragmentInHiltContainer<MainFragment>(fragmentFactory = TestFragmentFactory(mainViewModel))
    }


    @After
    fun tearDown() {
        mockWebServer.shutdown()
        IdlingRegistry.getInstance().unregister(ExpressoIdlingResource.idlingResource)
    }


    @Ignore("Resource of notification access")
    @Test
    fun notificationAccess() = runTest {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        with(manager.activeNotifications.first()) {
            println("${this.id} and ${this.notification.extras[Notification.EXTRA_TEXT]}")
        }

        onView(isRoot()).perform(object : ViewAction {
            override fun getDescription(): String = "Wait for delay"

            override fun getConstraints(): Matcher<View> = isRoot()

            override fun perform(uiController: UiController?, view: View?) {
                uiController?.loopMainThreadForAtLeast(3000)
            }

        })
    }


    @Test
    fun triggerOfflineNotification_checkStateViewModel_shouldSuccess() = runTest {

        mainViewModel.notificationTrigger.test {
            onView(withId(R.id.button)).perform(click())

            assertThat(awaitItem(), notNullValue())
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