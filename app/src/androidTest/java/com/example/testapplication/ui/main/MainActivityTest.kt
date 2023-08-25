package com.example.testapplication.ui.main

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.view.View
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.navigation.ui.setupWithNavController
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.filters.MediumTest
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import app.cash.turbine.test
import com.example.testapplication.R
import com.example.testapplication.ServiceLocator
import com.example.testapplication.configuration.TestCoroutineDispatcher
import com.example.testapplication.data.Result
import com.example.testapplication.data.model.NotificationModel
import com.example.testapplication.espresso.EspressoIdlingResource
import com.example.testapplication.data.source.DummyNotificationHelper
import com.example.testapplication.espresso.assertion.DelayView
import com.example.testapplication.espresso.assertion.RecyclerViewItemCount
import com.example.testapplication.espresso.assertion.RecyclerViewItemCount.Companion.withItemCount
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.stub
import java.util.Date
import javax.inject.Inject

/**
 * Should be test several point:
 * 1. Fragment navigation
 * 2. Active fragment
 * 3. Badge on navigation
 * 4. Error handler
 * */
@RunWith(AndroidJUnit4::class)
@UninstallModules(ServiceLocator::class)
@HiltAndroidTest
class MainActivityTest{
    private lateinit var context: Context

    @Inject
    lateinit var mockWebServer: MockWebServer

    @get:Rule
    var hiltAndroidRule = HiltAndroidRule(this)

    private lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun setUp() {
        hiltAndroidRule.inject()
        context = InstrumentationRegistry.getInstrumentation().targetContext
        IdlingRegistry.getInstance().register(EspressoIdlingResource.idlingResource)
    }


    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.idlingResource)
        mockWebServer.shutdown()
        if(this@MainActivityTest::scenario.isInitialized) scenario.close()
    }

    @SmallTest
    @Test
    fun a() = runTest {
        scenario = launchActivity()
        scenario.onActivity {
            if(it.mainViewModel !is FakeMainViewModel)
                throw IllegalAccessError("The ViewModel should be fake")
            assertThat(it.mainViewModel, instanceOf(FakeMainViewModel::class.java))
        }
    }


    @LargeTest
    @Test
    fun givenNavController_checkFragmentMove_shouldBeListFragment() = runTest {
        val navController = TestNavHostController(getApplicationContext())
        scenario = launchActivity()
        scenario.onActivity {
            navController.setGraph(R.navigation.main_nav)
            Navigation.setViewNavController(it.findViewById(R.id.nav_host_fragment), navController)
            navController.navigate(MainFragmentDirections.actionMainFragmentToListFragment())
        }

        EspressoIdlingResource.increment()
        assertThat(navController.currentDestination?.id, `is`(R.id.listFragment))
        EspressoIdlingResource.decrement()
    }


    @LargeTest
    @Test
    fun givenNotificationList_checkHotState_shouldSameSize() {
        val dummyValue = DummyNotificationHelper().getSeveral(5)

        scenario = launchActivity()
        onView(withId(R.id.listFragment)).perform(click())

        scenario.onActivity {
            (it.mainViewModel as FakeMainViewModel).forceUpdateList(dummyValue)
        }

        onView(withId(R.id.listFragment)).check(matches(isDisplayed()))

        onView(withId(R.id.recycler_view)).check(matches(isDisplayed()))
    }


    @MediumTest
    @Test
    fun givenNotificationList_checkBadgesCount_shouldTheSame() {
        val notificationList = listOf(
            NotificationModel(id = 1, mealId = 1, arrived = Date(), isSeen = true),
            NotificationModel(id = 2, mealId = 1, arrived = Date(), isSeen = false),
            NotificationModel(id = 3, mealId = 1, arrived = Date(), isSeen = false),
        )
        var badgeCount: Int? = 99

        scenario = launchActivity()
        scenario.onActivity {
            (it.mainViewModel as FakeMainViewModel).forceUpdateList(notificationList)
        }

        scenario.onActivity {
            badgeCount = it.navigation.getBadge(R.id.listFragment)?.number ?: 0
        }

        assertThat(badgeCount, `is`(2))
    }


    @SmallTest
    @Test
    fun givenNotificationTrigger_checkNotificationState_shouldSuccess() = runTest {
        scenario = launchActivity()
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        scenario.onActivity {
            (it.mainViewModel as FakeMainViewModel).triggerOfflineNotification()
        }

        EspressoIdlingResource.increment()
        with(manager.activeNotifications.first()) {
            EspressoIdlingResource.decrement()
            assertThat(this.id, `is`(11))
        }

    }


    @MediumTest
    @Test
    fun givenOnlineNotificationTrigger_checkNotificationState_shouldError() {
        val response = MockResponse()
            .setBody("server crashed")
            .setResponseCode(400)
        mockWebServer.enqueue(response)
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        scenario = launchActivity()
        onView(withId(R.id.button2)).perform(click())

        EspressoIdlingResource.increment()
        with(manager.activeNotifications.first()) {
            EspressoIdlingResource.decrement()
            assertThat(this.id, `is`(11)) // this happened because its retrieve default value
        }
    }


    @Ignore("Resource of notification access")
    @Test
    fun notificationAccess() = runTest {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        with(manager.activeNotifications.first()) {
            println("${this.id} and ${this.notification.extras[Notification.EXTRA_TEXT]}")
        }

        onView(ViewMatchers.isRoot()).perform(DelayView(1000))
    }

}