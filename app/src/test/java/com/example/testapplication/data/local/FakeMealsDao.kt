package com.example.testapplication.data.local

import android.content.res.Resources.NotFoundException
import com.example.testapplication.api.Meals
import com.example.testapplication.data.local.entity.MealsEntity

class FakeMealsDao(
    private val dummyMeals: MutableList<MealsEntity>
): MealsDao {
    override fun findAll(): List<MealsEntity> {
        return dummyMeals
    }

    override fun saveOneMeal(meals: MealsEntity) {
        dummyMeals.add(meals)
    }

    override fun findOne(id: Int): MealsEntity {
        return dummyMeals.find { it.id == id } ?: throw NotFoundException("Meals Not Found")
    }

    override fun findRandom(): MealsEntity {
        return dummyMeals.random()
    }
}