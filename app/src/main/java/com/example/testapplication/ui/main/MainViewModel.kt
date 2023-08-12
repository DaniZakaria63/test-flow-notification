package com.example.testapplication.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.testapplication.DefaultDispatcherProvider
import com.example.testapplication.DispatcherProvider
import com.example.testapplication.TestApp
import com.example.testapplication.api.Meals
import com.example.testapplication.data.Repository
import com.example.testapplication.data.Result
import com.example.testapplication.data.model.NotificationModel
import com.example.testapplication.data.source.DataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: DataRepository,
    private val dispatcher: DispatcherProvider
) : ViewModel() {

    val allListData: Flow<Result<List<NotificationModel>>>
        get() = repository.getAllNotification()

    /* asynchronous respond of one meal data */
    private val _mealState = MutableStateFlow<Result<Meals>>(Result.Loading)
    val mealState: StateFlow<Result<Meals>>
        get() = _mealState.asStateFlow()


    /* communication of exception event */
    private val _exceptionState = MutableSharedFlow<String>(replay = 0)
    val exceptionState: SharedFlow<String> get() = _exceptionState.asSharedFlow()


    /* notification trigger from MainFragment */
    private val _notificationTrigger = MutableSharedFlow<NotificationModel>()
    val notificationTrigger: SharedFlow<NotificationModel>
        get() = _notificationTrigger.asSharedFlow()
            .shareIn(viewModelScope, SharingStarted.WhileSubscribed())


    /* communication of detail notification */
    private val _intentExtra = MutableSharedFlow<Map<String, Int>>(replay = 0)
    val intentExtra: SharedFlow<Map<String, Int>>
        get() = _intentExtra.asSharedFlow()
            .shareIn(viewModelScope, SharingStarted.WhileSubscribed())


    fun triggerNotification(model: NotificationModel) {
        viewModelScope.launch(dispatcher.main) {
            _notificationTrigger.emit(model)
            repository.saveLocalNotification(model)
        }
    }

    fun setIntentExtra(key: String, value: Int) {
        viewModelScope.launch(dispatcher.main) {
            _intentExtra.emit(mapOf(key to value))
        }
    }

    fun triggerPushOnlineNotif() {
        viewModelScope.launch(dispatcher.io) {
            repository.callApiRandomDish()
                .catch { exception ->
                    _mealState.value = Result.Error(Exception(exception))
                    _exceptionState.emit(exception.message.toString())
                }.collect { meals -> _mealState.value = meals }
        }
    }

    /* Deprecated
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val repository: Repository = (this[APPLICATION_KEY] as TestApp).repository
                MainViewModel(repository)
            }
        }
    }
     */
}