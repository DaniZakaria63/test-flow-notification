package com.example.testapplication.data.api

import com.example.testapplication.data.model.Meals
import com.example.testapplication.data.model.ResultMeal
import com.google.gson.Gson
import okio.FileNotFoundException
import java.io.BufferedReader
import java.io.FileReader
import kotlin.jvm.Throws

object FakeMealsToJson {
    val baseSingleMeals: Meals get() = parseBaseMeals()
    val anotherSingleMeals: Meals = Meals(idMeal = 99)

    @Throws(FileNotFoundException::class)
    private fun parseBaseMeals(): Meals {
        val fileReader = BufferedReader(FileReader("single_meals_200.json"))
        val singleResultMeal = Gson().fromJson(fileReader, ResultMeal::class.java)
        fileReader.close()
        return singleResultMeal.meals.first()
    }

    fun makeItDouble(times: Int = 2): ResultMeal {
        val data = mutableListOf<Meals>()
        repeat(times) {
            data.add(baseSingleMeals)
        }
        return ResultMeal(data.toList())
    }

    fun anotherSingleToJson(): String = Gson().toJson(ResultMeal(listOf(anotherSingleMeals)))

    fun baseSingleMealsToJson(): String = Gson().toJson(baseSingleMeals)
}