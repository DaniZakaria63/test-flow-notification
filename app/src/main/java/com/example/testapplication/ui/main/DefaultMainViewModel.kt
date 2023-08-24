package com.example.testapplication.ui.main

import androidx.lifecycle.viewModelScope
import com.example.testapplication.DispatcherProvider
import com.example.testapplication.data.Result
import com.example.testapplication.data.UiState
import com.example.testapplication.data.source.DataRepository
import com.example.testapplication.data.source.DummyNotificationHelper
import com.example.testapplication.ui.base.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch


class DefaultMainViewModel constructor(
    repository: DataRepository,
    dispatcher: DispatcherProvider
) : MainViewModel(repository, dispatcher) {

    override fun setIntentExtra(key: String, value: Int) {
        viewModelScope.launch(dispatcher.main) {
            _intentExtra.emit(mapOf(key to value))
        }
    }


    override fun triggerPushOnlineNotify() {
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


    override fun triggerOfflineNotification() {
        errorHandler {
            val oneData = DummyNotificationHelper().getOne()
            _notificationTrigger.emit(oneData)
            repository.saveLocalNotification(oneData)
        }
    }


    override fun refreshNotificationList(refreshed: Boolean) {
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


    override fun updateNotifySeenStatus() {
        errorHandler{
            repository.updateNotifSeenStatus(true)
        }
    }


    fun clearNotificationList() {
        errorHandler {
            _allListData.emit(UiState(isLoading = true))
        }
    }
}