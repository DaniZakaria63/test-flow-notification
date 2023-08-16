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
    private val _statusState = MutableSharedFlow<Result<Boolean>>(replay = 0)
    val statusState: SharedFlow<Result<Boolean>>
        get() = _statusState.asSharedFlow()
            .shareIn(viewModelScope, SharingStarted.WhileSubscribed())


    /* communication of detail notification */
    private val _intentExtra = MutableSharedFlow<Map<String, Int>>()
    val intentExtra: SharedFlow<Map<String, Int>>
        get() = _intentExtra.asSharedFlow()
            .shareIn(viewModelScope, SharingStarted.WhileSubscribed())


    /* notification trigger from MainFragment */
    private val _notificationTrigger = MutableSharedFlow<NotificationModel>()
    val notificationTrigger: SharedFlow<NotificationModel>
        get() = _notificationTrigger.asSharedFlow()
            .shareIn(viewModelScope, SharingStarted.Lazily)


    fun setIntentExtra(key: String, value: Int) {
        viewModelScope.launch(dispatcher.main) {
            _intentExtra.emit(mapOf(key to value))
        }
    }


    fun triggerPushOnlineNotify() {
        viewModelScope.launch(dispatcher.main) {
            repository.callApiRandomDish()
                .flowOn(dispatcher.io)
                .catch { _statusState.emit(Result.Error(it)) }
                .collect { meals ->
                    when(meals){
                        is Result.Success -> {
                            val data = meals.data.asNotificationModel()
                            _notificationTrigger.emit(data)
                            repository.saveLocalNotification(data)
                        }
                        is Result.Error -> _statusState.emit(Result.Error(meals.exception))
                        Result.Loading -> _statusState.emit(Result.Loading)
                    }
                }
        }
    }


    fun triggerOfflineNotification() {
        errorHandler {
            _notificationTrigger.emit(DummyNotificationHelper().getOne())
        }
    }


    fun refreshNotificationList(refreshed: Boolean = false) {
        viewModelScope.launch(dispatcher.main) {
            _allListData.emit(UiState(isLoading = true))
            if (refreshed) delay(2_000)

            repository.getAllNotification()
                .flowOn(dispatcher.io)
                .onStart { _allListData.emit(UiState(isLoading = true)) }
                .catch { _allListData.emit(UiState(isError = false)) }
                .collect { models ->
                    when (models) {
                        is Result.Success -> _allListData.emit(
                            UiState(dataList = models.data.sortedByDescending { it.id })
                        )

                        is Result.Error -> _allListData.emit(UiState(isError = true))
                        Result.Loading -> _allListData.emit(UiState(isLoading = true))
                    }
                }
        }
    }


    fun updateNotifySeenStatus() {
        errorHandler{
            repository.updateNotifSeenStatus(true)
        }
    }


    fun clearNotificationList() {
        errorHandler {
            _allListData.emit(UiState(isLoading = true))
        }
    }

    private val errorHandler: ( suspend () -> Unit) -> Unit = { emit ->
        viewModelScope.launch(dispatcher.main) {
            try {
                emit()
            }catch (e: Throwable){
                _statusState.emit(Result.Error(e))
            }
        }
    }
}