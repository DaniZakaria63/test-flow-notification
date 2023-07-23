package com.example.testapplication.util

import java.text.SimpleDateFormat
import java.util.Date

object StringOperation {
    private const val MAX_TITLE_COUNT: Int = 11
    private const val DATE_PATTERN_TIME = "HH:mm:ss"
    fun cutLetter(value: String): String {
        val replaced = if (value.length > 11) value.replaceRange(
            MAX_TITLE_COUNT,
            value.length,
            ""
        ) else value.uppercase()
        return "$replaced..."
    }

    /*To format date into time, or based on this link
    * https://stackoverflow.com/questions/62983990/date-and-time-in-kotlin
    * */
    fun dateFormatTime(value: Date): String {
        val dateFormat = value.time
        return dateFormat.toString()
    }

    fun parseInstruction(instructions: String?): String {
        return if (instructions.isNullOrEmpty()) "-" else instructions.replace(
            "\\n", System.getProperty("line.separator")
        )
    }
}