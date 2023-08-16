package com.example.testapplication.data.model

data class ResultMeal(val meals: List<Meals>) {
    fun getFirst(): Meals = meals[0]
}
