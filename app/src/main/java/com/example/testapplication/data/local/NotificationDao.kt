package com.example.testapplication.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.testapplication.data.local.entity.MealsEntity
import com.example.testapplication.data.local.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notification ORDER BY id DESC")
    fun findAll() : Flow<NotificationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveOneMeal(meals: MealsEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveOneNotification(notification: NotificationEntity)
}