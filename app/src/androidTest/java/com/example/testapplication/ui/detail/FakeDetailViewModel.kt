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
    private val currentMeal = Meals(idMeal = 11)

    override fun callDetail(mealId: Int) {
        viewModelScope.launch(dispatcher.main) {
            _mealData.emit(currentMeal)
        }
    }

    override fun updateClickedStatus(mealId: Int) {

    }
}