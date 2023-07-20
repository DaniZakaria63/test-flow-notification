package com.example.testapplication.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.testapplication.TestApp
import com.example.testapplication.api.Meals
import com.example.testapplication.data.Repository
import com.example.testapplication.data.Result
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class MainViewModel(private val repository: Repository) : ViewModel() {
    private val _mealState = MutableStateFlow(Meals())
    val mealState : StateFlow<Meals> get() = _mealState

    private val _exception = MutableSharedFlow<String>(replay = 0)
    val exceptionState : SharedFlow<String> get() = _exception

    private val _isLoading = MutableSharedFlow<Boolean>(replay = 0)
    val isLoading : SharedFlow<Boolean> get() = _isLoading

    fun triggerPushOnlineNotif(){
        viewModelScope.launch {
            repository.callApiRandomDish()
                .catch { exception ->
                    _exception.emit("Something Error")
                    exception.printStackTrace()
                }
                .collect{meals ->
                    when(meals){
                        is Result.Success -> _mealState.value = meals.data
                        is Result.Error -> _exception.emit("Something Error")
                        Result.Loading -> _isLoading.emit(true)
                    }
                }
        }
    }

    companion object {
        val Factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val repository : Repository = (this[APPLICATION_KEY] as TestApp).repository
                MainViewModel(repository)
            }
        }
    }
}