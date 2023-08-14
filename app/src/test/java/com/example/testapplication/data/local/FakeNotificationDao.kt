package com.example.testapplication.data.local

import com.example.testapplication.data.local.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Date

class FakeNotificationDao(
    private val dummyNotification: MutableList<NotificationEntity>
): NotificationDao {

    private val shouldReturnError : Boolean = false

    override fun findAll(): Flow<List<NotificationEntity>> = flow {
        if(shouldReturnError) throw IllegalStateException("ShouldReturnError, error happened")
        emit(dummyNotification)
    }

    override fun saveOneNotification(notification: NotificationEntity) {
        if(shouldReturnError) throw IllegalStateException("ShouldReturnError, error happened")
        dummyNotification.add(notification)
    }
}