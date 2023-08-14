package com.example.testapplication.data

import com.example.testapplication.TestCoroutineDispatcher
import com.example.testapplication.api.Meals
import com.example.testapplication.data.api.FakeMealRemoteDataSource
import com.example.testapplication.data.local.FakeMealsDao
import com.example.testapplication.data.local.FakeNotificationDao
import com.example.testapplication.data.local.entity.MealsEntity
import com.example.testapplication.data.local.entity.NotificationEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class RepositoryTest {
    private lateinit var repository: Repository
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
    fun setUp() {
        val fakeRemoteSource = FakeMealRemoteDataSource(dummyMeals.toApiModel().toMutableList())
        val fakeMealsDao = FakeMealsDao(dummyMeals.toMutableList())
        val fakeNotificationDao = FakeNotificationDao(dummyNotification)
        repository = Repository(
            fakeRemoteSource,
            fakeNotificationDao,
            fakeMealsDao,
            TestCoroutineDispatcher()
        )
    }

    @Test
    fun givenApiSource_retrieveAll_returnSuccess(){

    }
}

fun List<MealsEntity>.toApiModel() : List<Meals> {
    return this.map {
        Meals(
            idMeal = it.id,
            strMeal = it.strMeal,
            strDrinkAlternate = it.strDrinkAlternate,
            strCategory = it.strCategory,
            strArea = it.strArea,
            strInstructions = it.strInstructions,
            strMealThumb = it.strMealThumb,
            strTags = it.strTags,
            strYoutube = it.strYoutube,
            strSource = it.strSource,
            strImageSource = it.strImageSource,
            strCreativeCommonsConfirmed = it.strCreativeCommonsConfirmed,
            dateModified = it.dateModified,
            strIngredient1 = it.strIngredient1,
            strIngredient2 = it.strIngredient2,
            strIngredient3 = it.strIngredient3,
            strIngredient4 = it.strIngredient4,
            strIngredient5 = it.strIngredient5,
            strIngredient6 = it.strIngredient6,
            strIngredient7 = it.strIngredient7,
            strIngredient8 = it.strIngredient8,
            strIngredient9 = it.strIngredient9,
            strIngredient10 = it.strIngredient10,
            strIngredient11 = it.strIngredient11,
            strIngredient12 = it.strIngredient12,
            strIngredient13 = it.strIngredient13,
            strIngredient14 = it.strIngredient14,
            strIngredient15 = it.strIngredient15,
            strIngredient16 = it.strIngredient16,
            strIngredient17 = it.strIngredient17,
            strIngredient18 = it.strIngredient18,
            strIngredient19 = it.strIngredient19,
            strIngredient20 = it.strIngredient20,
            strMeasure1 = it.strMeasure1,
            strMeasure2 = it.strMeasure2,
            strMeasure3 = it.strMeasure3,
            strMeasure4 = it.strMeasure4,
            strMeasure5 = it.strMeasure5,
            strMeasure6 = it.strMeasure6,
            strMeasure7 = it.strMeasure7,
            strMeasure8 = it.strMeasure8,
            strMeasure9 = it.strMeasure9,
            strMeasure10 = it.strMeasure10,
            strMeasure11 = it.strMeasure11,
            strMeasure12 = it.strMeasure12,
            strMeasure13 = it.strMeasure13,
            strMeasure14 = it.strMeasure14,
            strMeasure15 = it.strMeasure15,
            strMeasure16 = it.strMeasure16,
            strMeasure17 = it.strMeasure17,
            strMeasure18 = it.strMeasure18,
            strMeasure19 = it.strMeasure19,
            strMeasure20 = it.strMeasure20
        )
    }
}