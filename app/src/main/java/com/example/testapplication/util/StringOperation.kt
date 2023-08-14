package com.example.testapplication.util

import java.util.Date

object StringOperation {
    private const val MAX_TITLE_COUNT: Int = 64
    private const val DATE_PATTERN_TIME = "HH:mm:ss"
    fun cutLetter(value: String): String {
        return if (value.length > 64) value.replaceRange(
            MAX_TITLE_COUNT,
            value.length,
            ""
        ) + "..." else value
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