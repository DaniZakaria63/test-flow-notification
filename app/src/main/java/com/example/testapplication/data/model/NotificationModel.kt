package com.example.testapplication.data.model

import com.example.testapplication.data.local.entity.NotificationEntity
import com.example.testapplication.util.StringOperation
import java.util.Date

data class NotificationModel(
    val id: Int = 0,
    val mealId: Int,
    val title: String = "",
    val body: String = "",
    val img_remote: String = "",
    val arrived: Date,
    val isClicked: Boolean = false,
    val isSeen: Boolean = false
) {
    val titleFormatted: String
        get() = StringOperation.cutLetter(title)

    val bodyFormatted: String
        get() = StringOperation.cutLetter(body)

    val dateFormatted: String
        get() = StringOperation.dateFormatTime(arrived)

    val active: Boolean
        get() = !isSeen

    fun asDatabaseModel(): NotificationEntity {
        return NotificationEntity(
            id, mealId, title, body, img_remote, arrived, isClicked, isSeen
        )
    }
}

fun List<NotificationEntity>.notifDbToModel(): List<NotificationModel> {
    return map {
        NotificationModel(
            it.id,
            it.mealId,
            it.title,
            it.body,
            it.img_remote,
            it.arrived,
            it.isClicked,
            it.isSeen
        )
    }
}