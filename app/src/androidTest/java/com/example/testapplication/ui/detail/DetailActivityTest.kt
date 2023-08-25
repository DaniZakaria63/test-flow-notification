package com.example.testapplication.ui.detail

import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.bumptech.glide.RequestManager
import com.example.testapplication.R
import com.example.testapplication.ServiceLocator
import com.example.testapplication.data.api.FakeMealsToJson
import com.example.testapplication.data.model.Meals
import com.example.testapplication.espresso.assertion.RecyclerViewItemCount.Companion.withItemCount
import com.example.testapplication.util.DefaultViewModelFactory
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@UninstallModules(ServiceLocator::class)
@HiltAndroidTest
class DetailActivityTest {
    private val fakeMeals = FakeMealsToJson
    private val oneData = Meals(
        idMeal = 99,
        strMeal = "What is this",
        strInstructions = "Nothing",
        strCategory = "no category",
        strArea = "no area",
        strMealThumb = "https://picsum.photos/200",
        strIngredient1 = "salt",
        strMeasure1 = "1mg",
        strIngredient2 = "sugar",
        strMeasure2 = "2mg",
        strIngredient3 = "vinegar",
        strMeasure3 = "1lt"
    )

    private val defaultResponse200 = MockResponse()
        .setBody(fakeMeals.customToJson(oneData))
        .setResponseCode(200)

    @Inject
    lateinit var mockWebServer: MockWebServer

    @get:Rule
    var hiltAndroidRule = HiltAndroidRule(this)

    lateinit var activityScenario: ActivityScenario<DetailActivity>

    private val intent = Intent(
        ApplicationProvider.getApplicationContext(),
        DetailActivity::class.java
    ).putExtra("ID",oneData.idMeal)

    @Before
    fun setUp() {
        hiltAndroidRule.inject()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        activityScenario.close()
    }

    @Test
    fun givenOneModel_checkNetworkData_shouldSuccess() = runTest {
        mockWebServer.enqueue(defaultResponse200)
        val scenario = launchActivity<DetailActivity>(intent)

        scenario.moveToState(Lifecycle.State.STARTED)
        onView(withId(R.id.txt_title)).check(matches(withText(oneData.strMeal)))
        onView(withId(R.id.rv_ingredients)).check(withItemCount(3))
        scenario.moveToState(Lifecycle.State.DESTROYED)
    }

    @Test
    fun givenErrorResult_checkMealData_shouldError() = runTest {
        val responseError_400 = MockResponse()
            .setBody("Server got typo on it")
            .setResponseCode(500)
        mockWebServer.enqueue(responseError_400)
        val scenario = launchActivity<DetailActivity>(intent)

        scenario.moveToState(Lifecycle.State.STARTED)
    }

}