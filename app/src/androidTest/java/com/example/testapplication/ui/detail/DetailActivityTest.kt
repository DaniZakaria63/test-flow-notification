package com.example.testapplication.ui.detail

import android.content.ComponentName
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import androidx.test.filters.LargeTest
import com.bumptech.glide.RequestManager
import com.example.testapplication.ServiceLocator
import com.example.testapplication.data.api.FakeMealsToJson
import com.example.testapplication.data.model.Meals
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.isNotNull
import javax.inject.Inject

@LargeTest
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

    private val intent = Intent.makeMainActivity(ComponentName(
        ApplicationProvider.getApplicationContext(),
        DetailActivity::class.java
    )).putExtra("ID",oneData.idMeal)

    private val defaultResponse200 = MockResponse()
        .setBody(fakeMeals.customToJson(oneData))
        .setResponseCode(200)

//    @BindValue
//    @JvmField
//    val detailViewModel = mock<DetailViewModel>()

    @Inject
    lateinit var glideInstance: RequestManager

    @Inject
    lateinit var mockWebServer: MockWebServer

    @get:Rule
    var hiltAndroidRule = HiltAndroidRule(this)

    lateinit var activityScenario: ActivityScenario<DetailActivity>

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
        var idValue: Int?
        mockWebServer.enqueue(defaultResponse200)
        val scenario = launchActivity<DetailActivity>(intent)
//        scenario.recreate()
//        scenario.use {
//            idValue = intent.getIntExtra("ID", 0)
//        }
//
//        assertThat(idValue, isNotNull())
    }

}