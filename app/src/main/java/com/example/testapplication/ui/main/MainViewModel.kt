package com.example.testapplication.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testapplication.DispatcherProvider
import com.example.testapplication.data.Result
import com.example.testapplication.data.model.NotificationModel
import com.example.testapplication.data.source.DataRepository
import com.example.testapplication.data.source.DummyNotificationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: DataRepository,
    private val dispatcher: DispatcherProvider
) : ViewModel() {

    private val _allListData: MutableStateFlow<Result<List<NotificationModel>>> =
        MutableStateFlow(Result.Loading)
    val allListData: StateFlow<Result<List<NotificationModel>>>
        get() = _allListData.asStateFlow()
            .stateIn(viewModelScope, SharingStarted.Lazily, Result.Loading)

    /* communication of exception event */
    private val _exceptionState = MutableSharedFlow<String>(replay = 0)
    val exceptionState: SharedFlow<String> get() = _exceptionState.asSharedFlow()


    /* communication of detail notification */
    private val _intentExtra = MutableSharedFlow<Map<String, Int>>()
    val intentExtra: SharedFlow<Map<String, Int>>
        get() = _intentExtra.asSharedFlow()
            .shareIn(viewModelScope, SharingStarted.WhileSubscribed())


    /* notification trigger from MainFragment */
    private val _notificationTrigger = MutableSharedFlow<NotificationModel>()
    val notificationTrigger: SharedFlow<NotificationModel>
        get() = _notificationTrigger.asSharedFlow()
            .onEach { model ->
                repository.saveLocalNotification(model)
            }
            .shareIn(viewModelScope, SharingStarted.WhileSubscribed())


    fun setIntentExtra(key: String, value: Int) {
        viewModelScope.launch(dispatcher.main) {
            _intentExtra.emit(mapOf(key to value))
        }
    }


    fun triggerPushOnlineNotif() {
        viewModelScope.launch(dispatcher.main) {
            repository.callApiRandomDish()
                .catch { exception ->
                    _exceptionState.emit(exception.message.toString())
                }.collect { meals ->
                    if (meals is Result.Success) {
                        _notificationTrigger.emit(meals.data.asNotificationModel())
                    }
                }
        }
    }


    fun triggerOfflineNotification() {
        viewModelScope.launch(dispatcher.main) {
            _notificationTrigger.emit(DummyNotificationHelper().getOne())
        }
    }


    fun refreshNotificationList(refreshed: Boolean = false) {
        viewModelScope.launch(dispatcher.default) {
            _allListData.emit(Result.Loading)
            if (refreshed) delay(2_000)

            repository.getAllNotification()
                .onStart { Result.Loading }
                .catch { Result.Error(it) }
                .collect { models -> _allListData.emit(models) }
        }
    }

    fun clearNotificationList(){
        viewModelScope.launch (dispatcher.main) {
            _allListData.emit(Result.Loading)
        }
    }
}