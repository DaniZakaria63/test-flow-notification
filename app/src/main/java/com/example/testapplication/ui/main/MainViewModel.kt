package com.example.testapplication.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testapplication.DispatcherProvider
import com.example.testapplication.data.Result
import com.example.testapplication.data.UiState
import com.example.testapplication.data.model.Meals
import com.example.testapplication.data.model.NotificationModel
import com.example.testapplication.data.source.DataRepository
import com.example.testapplication.data.source.DummyNotificationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
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

    private val _allListData: MutableStateFlow<UiState<NotificationModel>> =
        MutableStateFlow(UiState(isLoading = true))
    val allListData: StateFlow<UiState<NotificationModel>>
        get() = _allListData.asStateFlow()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), UiState(isLoading = true))


    /* badges notification */
    val badgesCount: SharedFlow<Int> = _allListData
        .map { state ->
            val datas = state.dataList?.filter { !it.isSeen }
            datas?.size ?: 0
        }
        .flowOn(dispatcher.io)
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed())


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


    fun triggerPushOnlineNotify() {
        viewModelScope.launch(dispatcher.main) {
            repository.callApiRandomDish()
                .flowOn(dispatcher.io)
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
        viewModelScope.launch(dispatcher.main) {
            _allListData.emit(UiState(isLoading = true))
            if (refreshed) delay(2_000)

            repository.getAllNotification()
                .flowOn(dispatcher.io)
                .onStart { UiState<List<Meals>>(isLoading = true) }
                .catch { UiState<List<Meals>>(isLoading = false) }
                .collect { models ->
                    if (models is Result.Success) _allListData.emit(
                        UiState(dataList = models.data.sortedByDescending { it.id })
                    )
                }
        }
    }


    fun updateNotifySeenStatus() {
        viewModelScope.launch(dispatcher.main) {
            repository.updateNotifSeenStatus(true)
        }
    }


    fun clearNotificationList() {
        viewModelScope.launch(dispatcher.main) {
            _allListData.emit(UiState(isLoading = true))
        }
    }
}