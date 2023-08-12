package com.example.testapplication.ui.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.testapplication.DispatcherProvider
import com.example.testapplication.TestApp
import com.example.testapplication.api.Meals
import com.example.testapplication.data.Repository
import com.example.testapplication.data.Result
import com.example.testapplication.data.source.DataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: DataRepository,
    private val dispatcher: DispatcherProvider
) : ViewModel() {
    private val _mealData : MutableLiveData<Meals> = MutableLiveData<Meals>(Meals(0))
    val mealData : LiveData<Meals> get() = _mealData

    private val _imageMeal : MutableLiveData<String> = MutableLiveData()
    val imageMeal : LiveData<String> get() = _imageMeal

    private val _exception : MutableSharedFlow<String> = MutableSharedFlow(replay = 0)
    val exception : SharedFlow<String> get() = _exception

    private val _loading : MutableSharedFlow<Boolean> = MutableSharedFlow()
    val loading : SharedFlow<Boolean> get() = _loading

    fun callDetail(mealId: Int) {
        viewModelScope.launch(dispatcher.io) {
            repository.getDetailMeal(mealId)
                .catch {error ->
                    _exception.emit(error.message.toString())
                }.collect{ meal ->
                    when(meal){
                        is Result.Success -> {
                            _mealData.value = meal.data!!
                            _imageMeal.value = meal.data.strMealThumb!!
                        }
                        is Result.Error -> _exception.emit(meal.exception.toString())
                        Result.Loading -> _loading.emit(true)
                    }
                }
        }
    }

    /*
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