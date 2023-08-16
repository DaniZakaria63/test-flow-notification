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

    @Query("SELECT * FROM meals WHERE id = :id")
    fun findOne(id: Int): MealsEntity

    @Query("SELECT * FROM meals ORDER BY RANDOM() LIMIT 1")
    fun findRandom(): MealsEntity
}