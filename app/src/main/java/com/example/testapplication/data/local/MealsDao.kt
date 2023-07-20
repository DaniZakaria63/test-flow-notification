package com.example.testapplication.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.testapplication.data.local.entity.MealsEntity

@Dao
interface MealsDao {
    @Query("SELECT * FROM meals")
    fun findAll() : List<MealsEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveOneMeal(meals: MealsEntity)
}