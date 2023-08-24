package com.example.testapplication.ui.detail

import androidx.lifecycle.viewModelScope
import com.example.testapplication.DispatcherProvider
import com.example.testapplication.data.Result
import com.example.testapplication.data.model.Meals
import com.example.testapplication.data.source.DataRepository
import com.example.testapplication.ui.base.DetailViewModel
import kotlinx.coroutines.launch

class FakeDetailViewModel constructor(
    repository: DataRepository,
    dispatcher: DispatcherProvider
): DetailViewModel(repository, dispatcher) {

    override fun callDetail(mealId: Int) {
        viewModelScope.launch(dispatcher.main) {
            repository.getDetailMeal(mealId).collect{ meals ->
                when(meals){
                    is Result.Success -> _mealData.emit(meals.data)
                    is Result.Error -> _mealData.emit(Meals(idMeal = 11))
                    Result.Loading -> _mealData.emit(Meals(idMeal = 1))
                }
            }
        }
    }

    override fun updateClickedStatus(mealId: Int) {
        viewModelScope.launch(dispatcher.main) {
            repository.updateNotifClickedStatus(mealId)
        }
    }
}