package com.example.testapplication.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testapplication.DispatcherProvider
import com.example.testapplication.data.Repository
import com.example.testapplication.data.Result
import com.example.testapplication.data.UiState
import com.example.testapplication.data.model.NotificationModel
import com.example.testapplication.data.source.DataRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

abstract class MainViewModel(
    protected val repository: DataRepository,
    protected val dispatcher: DispatcherProvider
) : ViewModel(){

    protected val _allListData: MutableStateFlow<UiState<NotificationModel>> =
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
    protected val _statusState = MutableSharedFlow<Result<Boolean>>(replay = 0)
    val statusState: SharedFlow<Result<Boolean>>
        get() = _statusState.asSharedFlow()
            .shareIn(viewModelScope, SharingStarted.WhileSubscribed())


    /* communication of detail notification */
    protected val _intentExtra = MutableSharedFlow<Map<String, Int>>()
    val intentExtra: SharedFlow<Map<String, Int>>
        get() = _intentExtra.asSharedFlow()
            .shareIn(viewModelScope, SharingStarted.WhileSubscribed())


    /* notification trigger from MainFragment */
    protected val _notificationTrigger = MutableSharedFlow<NotificationModel>()
    val notificationTrigger: SharedFlow<NotificationModel>
        get() = _notificationTrigger.asSharedFlow()
            .shareIn(viewModelScope, SharingStarted.Lazily)

    protected val errorHandler: ( suspend () -> Unit) -> Unit = { emit ->
        viewModelScope.launch(dispatcher.main) {
            try {
                emit()
            }catch (e: Throwable){
                _statusState.emit(Result.Error(e))
            }
        }
    }

    abstract fun setIntentExtra(key: String, value: Int)
    abstract fun triggerPushOnlineNotify()
    abstract fun triggerOfflineNotification()
    abstract fun refreshNotificationList(refreshed: Boolean = false)
    abstract fun updateNotifySeenStatus()
}