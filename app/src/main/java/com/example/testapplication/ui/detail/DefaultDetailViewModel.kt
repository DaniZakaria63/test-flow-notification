package com.example.testapplication.ui.detail

import androidx.lifecycle.viewModelScope
import com.example.testapplication.DispatcherProvider
import com.example.testapplication.data.Result
import com.example.testapplication.data.model.Meals
import com.example.testapplication.data.source.DataRepository
import com.example.testapplication.ui.base.DetailViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class DefaultDetailViewModel constructor(
    repository: DataRepository,
    dispatcher: DispatcherProvider
) : DetailViewModel(repository, dispatcher) {
    override fun callDetail(mealId: Int) {
        viewModelScope.launch(dispatcher.main) {
            repository.getDetailMeal(mealId)
                .onStart { _statusState.emit(Result.Loading) }
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

    override fun updateClickedStatus(mealId: Int){
        viewModelScope.launch(dispatcher.main) {
            try {
                if(mealId==0) throw NullPointerException("No data that matches with mealId")
                repository.updateNotifClickedStatus(mealId)
            } catch (e: Throwable) {
                _statusState.emit(Result.Error(e))
            }
        }
    }

}