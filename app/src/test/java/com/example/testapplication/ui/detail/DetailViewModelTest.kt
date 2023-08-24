package com.example.testapplication.ui.detail

import app.cash.turbine.test
import com.example.testapplication.TestCoroutineDispatcher
import com.example.testapplication.data.MainCoroutineRule
import com.example.testapplication.data.Repository
import com.example.testapplication.data.Result
import com.example.testapplication.data.model.Meals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import java.io.IOException


@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class DetailViewModelTest {
    private val dummyOneData = Meals(idMeal = 99)
    private lateinit var dispatcher: TestCoroutineDispatcher
    private lateinit var defaultDetailViewModel: DefaultDetailViewModel

    @Mock
    private lateinit var repository: Repository

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUp() {
        dispatcher = TestCoroutineDispatcher()
        defaultDetailViewModel = DefaultDetailViewModel(repository, dispatcher)
    }

    @Test
    fun givenSuccess_callDetail_shouldSuccess() = runTest {
        // Given repository returning success value
        doReturn(
            flowOf<Result<Meals>>(
                Result.Success(dummyOneData)
            )
        ).`when`(repository).getDetailMeal(anyInt())

        // When trigger
        defaultDetailViewModel.callDetail(anyInt())

        // Then check all these things going
        defaultDetailViewModel.mealData.test {
            assertThat(awaitItem(), equalTo(dummyOneData)) // incoming data event
            cancelAndIgnoreRemainingEvents()
        }

        defaultDetailViewModel.statusState.test {
            assertThat(awaitItem(), instanceOf(Result.Loading::class.java)) // was never called
            cancelAndIgnoreRemainingEvents()
        }

        verify(repository).getDetailMeal(anyInt())
    }


    @Test
    fun givenError_callDetail_shouldError() = runTest {
        val throwError = IOException("Hello")

        // Given repository returning error value
        doReturn(
            flowOf<Result<Meals>>(
                Result.Error(throwError)
            )
        ).`when`(repository).getDetailMeal(anyInt())

        // When triggering
        defaultDetailViewModel.callDetail(anyInt())

        // Then check the response
        defaultDetailViewModel.mealData.test {
            assertThat(awaitItem(), equalTo(Meals(0))) // default state
            cancelAndIgnoreRemainingEvents()
        }
        defaultDetailViewModel.statusState.test {
            assertThat(awaitItem(), equalTo(Result.Error(throwError)))
            cancelAndIgnoreRemainingEvents()
        }
        verify(repository).getDetailMeal(anyInt())
    }


    @Test
    fun givenLoading_callDetail_shouldLoading() = runTest {
        // Given repository still loading
        doReturn(
            flowOf<Result<Meals>>(
                Result.Loading
            )
        ).`when`(repository).getDetailMeal(anyInt())

        // When triggering
        defaultDetailViewModel.callDetail(anyInt())

        // Then check the response
        defaultDetailViewModel.statusState.test {
            assertThat(awaitItem(), equalTo(Result.Loading))
            cancelAndIgnoreRemainingEvents()
        }
        defaultDetailViewModel.mealData.test {
            assertThat(awaitItem(), equalTo(Meals(0))) // default state, should be
            cancelAndIgnoreRemainingEvents()
        }
        verify(repository).getDetailMeal(anyInt())
    }

    @Test
    fun givenUpdatedClicked_updateNotifStatus_shouldSuccess() = runTest {
        // Given repository oke
        doAnswer { args ->
            val data: Int = args.arguments[0] as Int

            // Then check if its happened
            assertThat(data, notNullValue())
            assertThat(data, `is`(dummyOneData.idMeal))
        }.`when`(repository).updateNotifClickedStatus(anyInt())

        // When trigger
        defaultDetailViewModel.updateClickedStatus(dummyOneData.idMeal)

        // Then
        verify(repository).updateNotifClickedStatus(anyInt())
    }


    @Test
    fun givenZeroId_updateNotifStatus_shouldThrownError() = runTest {
        // This for make sure the code won't reach the method, but need commented due to clean test
        /*
        doAnswer {
            throw IllegalAccessException("This should not be reached")
        }.`when`(repository).updateNotifClickedStatus(anyInt())
         */

        defaultDetailViewModel.updateClickedStatus(0)

        defaultDetailViewModel.statusState.test {
            // different object and given error, still wondering why that happened
            // assertThat(awaitItem(), `is`(Result.Error(throwError)))
            assertThat(awaitItem(), instanceOf(Result.Error::class.java))
            cancelAndIgnoreRemainingEvents()
        }
        verify(repository, times(0)).updateNotifClickedStatus(anyInt())
    }


    @Test
    fun givenThrowError_updateNotifStatus_shouldReturnError() = runTest {
        doAnswer {
            throw IOException("database closed")
        }.`when`(repository).updateNotifClickedStatus(anyInt())

        defaultDetailViewModel.updateClickedStatus(dummyOneData.idMeal)

        defaultDetailViewModel.statusState.test {
            assertThat(awaitItem(), instanceOf(Result.Error::class.java))
            cancelAndIgnoreRemainingEvents()
        }
        verify(repository).updateNotifClickedStatus(anyInt())
    }


    @Test
    fun givenIngredientsData_retrieveDummy_shouldReturnDefault() = runTest {
        val default = listOf<Pair<String,String>>()
        doReturn(flowOf<Result<Meals>>(
            Result.Loading
        )).`when`(repository).getDetailMeal(anyInt())

        defaultDetailViewModel.callDetail(anyInt())
        defaultDetailViewModel.statusState.test {
            assertThat(awaitItem(), `is`(Result.Loading))
            cancelAndIgnoreRemainingEvents()
        }
        defaultDetailViewModel.ingredientData.test {
            assertThat(awaitItem(), `is`(default))
            cancelAndIgnoreRemainingEvents()
        }
        verify(repository).getDetailMeal(anyInt())
    }


    @Test
    fun givenIngredientsData_retrieveIngredients_shouldSuccess() = runTest {
        val oneData = Meals(
            idMeal = 11,
            strIngredient1 = "salt",
            strMeasure1 = "1mg",
            strIngredient2 = "sugar",
            strMeasure2 = "2mg",
            strIngredient3 = "vinegar",
            strMeasure3 = "1lt"
        )

        doReturn(flowOf<Result<Meals>>(
            Result.Success(oneData)
        )).`when`(repository).getDetailMeal(anyInt())

        defaultDetailViewModel.callDetail(oneData.idMeal)
        defaultDetailViewModel.mealData.test {
            assertThat(awaitItem(), equalTo(oneData))
            cancelAndIgnoreRemainingEvents()
        }

        defaultDetailViewModel.ingredientData.test {
            val data = awaitItem()!! // given data
            assertThat(data.size, `is`(3))
            assertThat(data[0].first, `is`("salt"))
            assertThat(data[0].second, `is`("1mg"))
            assertThat(data[1].first, `is`("sugar"))
            assertThat(data[1].second, `is`("2mg"))
            assertThat(data[2].first, `is`("vinegar"))
            assertThat(data[2].second, `is`("1lt"))
            cancelAndIgnoreRemainingEvents()
        }

        verify(repository).getDetailMeal(anyInt())
    }
}