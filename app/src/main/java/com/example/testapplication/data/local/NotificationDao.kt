package com.example.testapplication.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.testapplication.data.local.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notification")
    fun findAll() : Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveOneNotification(notification: NotificationEntity)

    @Query("UPDATE notification SET seen = :seen")
    fun updateNotificationSeen(seen: Boolean)

    @Query("UPDATE notification SET clicked = :clicked WHERE meal_id = :mealId")
    fun updateNotifClickedByMealId(clicked: Boolean, mealId: Int)
}