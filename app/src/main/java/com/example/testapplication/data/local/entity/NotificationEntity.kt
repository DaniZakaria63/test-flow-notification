package com.example.testapplication.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.testapplication.util.StringOperation
import java.util.Date

@Entity(tableName = "notification")
data class NotificationEntity constructor(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "meal_id") val mealId: Int,
    @ColumnInfo(name = "title") val title: String = "",
    @ColumnInfo(name = "body") val body: String = "",
    @ColumnInfo(name = "img_remote") val img_remote: String = "",
    @ColumnInfo(name = "arrived") val arrived: Date,
    @ColumnInfo(name = "clicked") val isClicked: Boolean = false,
    @ColumnInfo(name = "seen") val isSeen: Boolean = false
) {
    val titleFormatted: String
        get() = StringOperation.cutLetter(title)

    val dateFormatted: String
        get() = StringOperation.dateFormatTime(arrived)

    val active: Boolean
        get() = !isSeen
}