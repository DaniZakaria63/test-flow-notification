package com.example.testapplication.data.local

import com.example.testapplication.data.local.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Date

class FakeNotificationDao: NotificationDao {
    private val dummyNotification = mutableListOf<NotificationEntity>(
        NotificationEntity(id = 0, mealId = 0, arrived = Date()),
        NotificationEntity(id = 1, mealId = 1, arrived = Date()),
        NotificationEntity(id = 2, mealId = 2, arrived = Date()),
    )

    override fun findAll(): Flow<List<NotificationEntity>> = flow {
        emit(dummyNotification)
    }

    override fun saveOneNotification(notification: NotificationEntity) {
        dummyNotification.add(notification)
    }
}