package com.example.testapplication.api

import com.example.testapplication.data.model.Meals

data class ResultMeal(val meals: List<Meals>) {
    fun getFirst(): Meals = meals[0]
}
