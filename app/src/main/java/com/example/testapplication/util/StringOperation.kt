package com.example.testapplication.util

import java.text.SimpleDateFormat
import java.util.Date

object StringOperation {
    private const val MAX_TITLE_COUNT : Int = 11
    private const val DATE_PATTERN_TIME = "HH:mm:ss"
    fun cutLetter(value: String): String {
        val replaced = value.replaceRange(0, MAX_TITLE_COUNT, "")
        return "$replaced..."
    }

    /*To format date into time, or based on this link
    * https://stackoverflow.com/questions/62983990/date-and-time-in-kotlin
    * TODO: implement parsing, or offset*/
    fun dateFormatTime(value: Date) : String{
        val dateFormat = value.time
        return dateFormat.toString()
    }
}