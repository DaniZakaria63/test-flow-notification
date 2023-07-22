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
import com.example.testapplication.data.model.NotificationModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.lang.Exception

class MainViewModel(private val repository: Repository) : ViewModel() {
    val allListData: Flow<Result<List<NotificationModel>>> get() = repository.getAllNotification()


    /* asynchronous respond of one meal data */
    private val _mealState = MutableStateFlow<Result<Meals>>(Result.Loading)
    val mealState: StateFlow<Result<Meals>> get() = _mealState


    /* communication of exception event */
    private val _exceptionState = MutableSharedFlow<String>(replay = 0)
    val exceptionState: SharedFlow<String> get() = _exceptionState


    /* notification trigger from MainFragment */
    private val _notificationTrigger = MutableSharedFlow<NotificationModel>()
    val notificationTrigger: SharedFlow<NotificationModel> get() = _notificationTrigger


    /* communication of detail notification */
    private val _intentExtra = MutableSharedFlow<Map<String, Int>>(replay = 0)
    val intentExtra: SharedFlow<Map<String, Int>> get() = _intentExtra


    fun triggerNotification(model: NotificationModel) {
        viewModelScope.launch {
            _notificationTrigger.emit(model)
            repository.saveLocalNotification(model)
        }
    }

    fun setIntentExtra(key: String, value: Int) {
        val args = HashMap<String, Int>().apply {
            put(key, value)
        }
        viewModelScope.launch {
            _intentExtra.emit(args)
        }
    }

    fun triggerPushOnlineNotif() {
        viewModelScope.launch {
            repository.callApiRandomDish()
                .catch { exception ->
                    _mealState.value = Result.Error(Exception(exception))
                    _exceptionState.emit(exception.message.toString())
                }.collect{ meals -> _mealState.value = meals }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val repository: Repository = (this[APPLICATION_KEY] as TestApp).repository
                MainViewModel(repository)
            }
        }
    }
}