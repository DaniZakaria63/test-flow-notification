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
import kotlinx.coroutines.GlobalScope
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
            .map { meals -> meals.parseIngredient() }
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

    fun updateClickedStatus(mealId: Int = 0){
        viewModelScope.launch(dispatcher.main) {
            try {
                if(mealId==0) throw NullPointerException("No data that matches with mealId")
                repository.updateNotifClickedStatus(mealId)
            } catch (e: Throwable) {
                _statusState.emit(Result.Error(e))
            }
        }
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