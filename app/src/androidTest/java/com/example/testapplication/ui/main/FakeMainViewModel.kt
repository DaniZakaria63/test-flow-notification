package com.example.testapplication.ui.main

import androidx.lifecycle.viewModelScope
import com.example.testapplication.DispatcherProvider
import com.example.testapplication.data.Result
import com.example.testapplication.data.UiState
import com.example.testapplication.data.model.NotificationModel
import com.example.testapplication.data.source.DataRepository
import com.example.testapplication.ui.base.MainViewModel
import kotlinx.coroutines.launch
import java.util.Date

class FakeMainViewModel constructor(
    repository: DataRepository,
    dispatcher: DispatcherProvider
) : MainViewModel(repository, dispatcher) {
    fun forceUpdateList(newList: List<NotificationModel>){
        viewModelScope.launch { _allListData.emit(UiState(dataList = newList)) }
    }

    override fun setIntentExtra(key: String, value: Int) {
        viewModelScope.launch { _intentExtra.emit(mapOf(key to value)) }
    }

    override fun triggerPushOnlineNotify() {
        viewModelScope.launch {
            repository.callApiRandomDish().collect { result ->
                when(result){
                    is Result.Success -> _notificationTrigger.emit(NotificationModel(id = 11, mealId = 1, arrived = Date()))
                    is Result.Error -> _notificationTrigger.emit(NotificationModel(id = 99, mealId = 1, arrived = Date()))
                    Result.Loading -> _notificationTrigger.emit(NotificationModel(id = 1, mealId = 1, arrived = Date()))
                }
            }
        }
    }

    override fun triggerOfflineNotification() {
        viewModelScope.launch{
            _notificationTrigger.emit(NotificationModel(id = 11, mealId = 1, arrived = Date()))
        }
    }

    override fun refreshNotificationList(refreshed: Boolean) {
        viewModelScope.launch {
            repository.getAllNotification().collect{ result ->
                when(result){
                    is Result.Success -> _allListData.emit(UiState(dataList = result.data))
                    is Result.Error -> _allListData.emit(UiState(isError = true))
                    Result.Loading -> _allListData.emit(UiState(isLoading = true))
                }
            }
        }
    }

    override fun updateNotifySeenStatus() {
        viewModelScope.launch {
            repository.updateNotifSeenStatus(true)
            refreshNotificationList()
        }
    }

}