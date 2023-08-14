package com.example.testapplication.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.testapplication.BuildConfig.TAG
import com.example.testapplication.DispatcherProvider
import com.example.testapplication.data.Result
import com.example.testapplication.data.model.Meals
import com.example.testapplication.data.source.DataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: DataRepository,
    private val dispatcher: DispatcherProvider
) : ViewModel() {
    private val _mealData: MutableStateFlow<Meals> = MutableStateFlow(Meals(0))
    val mealData: StateFlow<Meals>
        get() = _mealData.stateIn(viewModelScope, SharingStarted.Lazily, Meals(0))

    val ingredientData: StateFlow<List<Pair<String, String>>?>
        get() = _mealData.asStateFlow()
            .map { meals -> parseIngredients(meals) }
            .stateIn(viewModelScope, SharingStarted.Lazily, listOf(Pair("-", "-")))


    private val _statusState: MutableStateFlow<Result<Boolean>> = MutableStateFlow(Result.Loading)
    val statusState: StateFlow<Result<Boolean>>
        get() = _statusState
            .asStateFlow()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Result.Loading)

    fun callDetail(mealId: Int) {
        viewModelScope.launch(dispatcher.main) {
            repository.getDetailMeal(mealId)
                .onStart { _statusState.emit(Result.Loading) }
                .onCompletion { _statusState.emit(Result.Success(true)) }
                .catch { _statusState.emit(Result.Error(it)) }
                .collect { meal: Result<Meals> ->
                    when (meal) {
                        is Result.Success -> { _mealData.emit(meal.data) }

                        is Result.Error -> _statusState.emit(Result.Error(meal.exception))
                        Result.Loading -> _statusState.emit(Result.Loading)
                    }
                }
        }
    }

    private fun parseIngredients(meal: Meals): List<Pair<String, String>> {
        val state = mutableListOf<Pair<String, String>>()
        val ingredients = listOf(
            meal.strIngredient1,
            meal.strIngredient2,
            meal.strIngredient3,
            meal.strIngredient4,
            meal.strIngredient5,
            meal.strIngredient6,
            meal.strIngredient7,
            meal.strIngredient8,
            meal.strIngredient9,
            meal.strIngredient10,
            meal.strIngredient11,
            meal.strIngredient12,
            meal.strIngredient13,
            meal.strIngredient14,
            meal.strIngredient15,
            meal.strIngredient16,
            meal.strIngredient17,
            meal.strIngredient18,
            meal.strIngredient19,
            meal.strIngredient20,
        )
        val measures = listOf(
            meal.strMeasure1,
            meal.strMeasure2,
            meal.strMeasure3,
            meal.strMeasure4,
            meal.strMeasure5,
            meal.strMeasure6,
            meal.strMeasure7,
            meal.strMeasure8,
            meal.strMeasure9,
            meal.strMeasure10,
            meal.strMeasure11,
            meal.strMeasure12,
            meal.strMeasure13,
            meal.strMeasure14,
            meal.strMeasure15,
            meal.strMeasure16,
            meal.strMeasure17,
            meal.strMeasure18,
            meal.strMeasure19,
            meal.strMeasure20
        )
        for (i in 1..20) {
            try {
                val ingredient =
                    if (ingredients[i].equals("")) throw NullPointerException() else ingredients[i]!!
                val measure =
                    if (measures[i].equals("")) throw NullPointerException() else measures[i]!!
                state.add(Pair(ingredient, measure))
            } catch (e: Exception) {
                Log.i(TAG, "parseIngredients: Skip this value")
            }
        }
        return state.toList()
    }


    /* Deprecated
    companion object{
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val repository: Repository = (this[APPLICATION_KEY] as TestApp).repository
                DetailViewModel(repository)
            }
        }
    }
     */
}