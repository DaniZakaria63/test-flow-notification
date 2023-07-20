package com.example.testapplication.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.testapplication.data.local.entity.MealsEntity
import com.example.testapplication.data.local.entity.NotificationEntity
import com.example.testapplication.util.DateConverter

/*Room need to use custom converter object data for Date
* based on, https://developer.android.com/training/data-storage/room/referencing-data
* */
@Database(entities = [NotificationEntity::class, MealsEntity::class], exportSchema = false, version = 1)
@TypeConverters(DateConverter::class)
abstract class NotificationDatabase : RoomDatabase() {
    abstract fun notificationDao(): NotificationDao

    companion object {
        const val DB_NAME = "test_notif"
    }
}