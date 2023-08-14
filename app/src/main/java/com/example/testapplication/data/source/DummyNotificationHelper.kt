package com.example.testapplication.data.source

import com.example.testapplication.data.model.NotificationModel
import java.util.Date

class DummyNotificationHelper {
    private var datas: ArrayList<NotificationModel> = ArrayList()

    init {
        addNotification("Congratulation! You've won!", "You have won grand prize for stay looking forward of your life", false, true)
        addNotification("What I Have Done!", "It takes myself to crash out what I becomes", false, false)
        addNotification("Never Gonna Give You Up", "Never gonna let you down", false, true)
        addNotification("Never Gonna ", "Run around and desert you", true, true)
        addNotification("Never Gonna Make You Cry", "Never gonna say goodbye", false, false)
        addNotification("Never Gonna", "Tell a lie and hurt you", true, false)
        addNotification("I Just Wanna Tell You", "How I am feeling", false, true)
        addNotification("Gotta Make You", "Understand", false, true)
        addNotification("Ohh Give You Up", "Never gonna give", true, true)
        addNotification("Never Gonna Give", "Ohh give you up", false, false)
    }

    fun getOne() : NotificationModel{
        val index = (0..9).random()
        return datas[index]
    }

    private fun addNotification(
        title: String,
        body: String,
        isClicked: Boolean,
        isSeen: Boolean,
        img_url: String = NOTIFICATION_DEFAULT_IMG_URL,
    ) {
        val index = (datas.size + 1)
        datas.add(
            NotificationModel(
                mealId = index,
                title = title,
                body = body,
                img_remote = img_url,
                arrived =  Date(),
                isClicked = isClicked,
                isSeen = isSeen
            )
        )
    }

    companion object {
        const val NOTIFICATION_DEFAULT_IMG_URL: String =
            "https://purepng.com/public/uploads/large/donuts-wbt.png"
    }
}