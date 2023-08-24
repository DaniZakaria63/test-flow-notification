package com.example.testapplication.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testapplication.DispatcherProvider
import com.example.testapplication.data.Repository
import com.example.testapplication.data.Result
import com.example.testapplication.data.model.Meals
import com.example.testapplication.data.source.DataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

abstract class DetailViewModel(
    protected val repository: DataRepository,
    protected val dispatcher: DispatcherProvider
) : ViewModel(){

    protected val _mealData: MutableStateFlow<Meals> = MutableStateFlow(Meals(0))
    val mealData: StateFlow<Meals>
        get() = _mealData.stateIn(viewModelScope, SharingStarted.Lazily, Meals(0))

    val ingredientData: StateFlow<List<Pair<String, String>>?>
        get() = _mealData.asStateFlow()
            .map { meals -> meals.parseIngredient() }
            .stateIn(viewModelScope, SharingStarted.Lazily, listOf(Pair("-", "-")))


    protected val _statusState: MutableStateFlow<Result<Boolean>> = MutableStateFlow(Result.Loading)
    val statusState: StateFlow<Result<Boolean>>
        get() = _statusState
            .asStateFlow()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Result.Loading)

    abstract fun callDetail(mealId: Int)
    abstract fun updateClickedStatus(mealId: Int = 0)
}