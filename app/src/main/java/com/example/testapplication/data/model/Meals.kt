package com.example.testapplication.data.model

import com.example.testapplication.data.local.entity.MealsEntity
import com.example.testapplication.util.StringOperation
import java.util.Date


data class Meals(
    val idMeal: Int,
    val strMeal: String? = "",
    val strDrinkAlternate: String? = "",
    val strCategory: String? = "",
    val strArea: String? = "",
    val strInstructions: String? = "",
    val strMealThumb: String? = "",
    val strTags: String? = "",
    val strYoutube: String? = "",
    val strSource: String? = "",
    val strImageSource: String? = "",
    val strCreativeCommonsConfirmed: String? = "",
    val dateModified: String? = "",
    val strIngredient1: String? = "",
    val strIngredient2: String? = "",
    val strIngredient3: String? = "",
    val strIngredient4: String? = "",
    val strIngredient5: String? = "",
    val strIngredient6: String? = "",
    val strIngredient7: String? = "",
    val strIngredient8: String? = "",
    val strIngredient9: String? = "",
    val strIngredient10: String? = "",
    val strIngredient11: String? = "",
    val strIngredient12: String? = "",
    val strIngredient13: String? = "",
    val strIngredient14: String? = "",
    val strIngredient15: String? = "",
    val strIngredient16: String? = "",
    val strIngredient17: String? = "",
    val strIngredient18: String? = "",
    val strIngredient19: String? = "",
    val strIngredient20: String? = "",
    val strMeasure1: String? = "",
    val strMeasure2: String? = "",
    val strMeasure3: String? = "",
    val strMeasure4: String? = "",
    val strMeasure5: String? = "",
    val strMeasure6: String? = "",
    val strMeasure7: String? = "",
    val strMeasure8: String? = "",
    val strMeasure9: String? = "",
    val strMeasure10: String? = "",
    val strMeasure11: String? = "",
    val strMeasure12: String? = "",
    val strMeasure13: String? = "",
    val strMeasure14: String? = "",
    val strMeasure15: String? = "",
    val strMeasure16: String? = "",
    val strMeasure17: String? = "",
    val strMeasure18: String? = "",
    val strMeasure19: String? = "",
    val strMeasure20: String? = "",
) {
    val strMealFormatted : String
        get() = StringOperation.cutLetter(strMeal.toString())

    val strInstructionsFormatted: String
        get() = StringOperation.parseInstruction(strInstructions)

    fun asNotificationModel(): NotificationModel {
        return NotificationModel(
            0,
            idMeal,
            strMeal.toString(),
            strInstructions.toString(),
            strMealThumb.toString(),
            Date(),
            false,
            false
        )
    }

    fun asDatabaseModel(): MealsEntity = MealsEntity(
        id = idMeal,
        strMeal = strMeal,
        strDrinkAlternate = strDrinkAlternate,
        strCategory = strCategory,
        strArea = strArea,
        strInstructions = strInstructions,
        strMealThumb = strMealThumb,
        strTags = strTags,
        strYoutube = strYoutube,
        strSource = strSource,
        strImageSource = strImageSource,
        strCreativeCommonsConfirmed = strCreativeCommonsConfirmed,
        dateModified = dateModified,
        strIngredient1 = strIngredient1,
        strIngredient2 = strIngredient2,
        strIngredient3 = strIngredient3,
        strIngredient4 = strIngredient4,
        strIngredient5 = strIngredient5,
        strIngredient6 = strIngredient6,
        strIngredient7 = strIngredient7,
        strIngredient8 = strIngredient8,
        strIngredient9 = strIngredient9,
        strIngredient10 = strIngredient10,
        strIngredient11 = strIngredient11,
        strIngredient12 = strIngredient12,
        strIngredient13 = strIngredient13,
        strIngredient14 = strIngredient14,
        strIngredient15 = strIngredient15,
        strIngredient16 = strIngredient16,
        strIngredient17 = strIngredient17,
        strIngredient18 = strIngredient18,
        strIngredient19 = strIngredient19,
        strIngredient20 = strIngredient20,
        strMeasure1 = strMeasure1,
        strMeasure2 = strMeasure2,
        strMeasure3 = strMeasure3,
        strMeasure4 = strMeasure4,
        strMeasure5 = strMeasure5,
        strMeasure6 = strMeasure6,
        strMeasure7 = strMeasure7,
        strMeasure8 = strMeasure8,
        strMeasure9 = strMeasure9,
        strMeasure10 = strMeasure10,
        strMeasure11 = strMeasure11,
        strMeasure12 = strMeasure12,
        strMeasure13 = strMeasure13,
        strMeasure14 = strMeasure14,
        strMeasure15 = strMeasure15,
        strMeasure16 = strMeasure16,
        strMeasure17 = strMeasure17,
        strMeasure18 = strMeasure18,
        strMeasure19 = strMeasure19,
        strMeasure20 = strMeasure20
    )

    fun parseIngredient() : List<Pair<String, String>> {
        val state = mutableListOf<Pair<String, String>>()
        val ingredients = listOf(
            strIngredient1,
            strIngredient2,
            strIngredient3,
            strIngredient4,
            strIngredient5,
            strIngredient6,
            strIngredient7,
            strIngredient8,
            strIngredient9,
            strIngredient10,
            strIngredient11,
            strIngredient12,
            strIngredient13,
            strIngredient14,
            strIngredient15,
            strIngredient16,
            strIngredient17,
            strIngredient18,
            strIngredient19,
            strIngredient20,
        )
        val measures = listOf(
            strMeasure1,
            strMeasure2,
            strMeasure3,
            strMeasure4,
            strMeasure5,
            strMeasure6,
            strMeasure7,
            strMeasure8,
            strMeasure9,
            strMeasure10,
            strMeasure11,
            strMeasure12,
            strMeasure13,
            strMeasure14,
            strMeasure15,
            strMeasure16,
            strMeasure17,
            strMeasure18,
            strMeasure19,
            strMeasure20
        )
        for (i in 0..19) {
            try {
                val ingredient =
                    if (ingredients[i].equals("")) throw NullPointerException() else ingredients[i]!!
                val measure =
                    if (measures[i].equals("")) throw NullPointerException() else measures[i]!!
                state.add(Pair(ingredient, measure))
            } catch (e: Exception) {
            }
        }
        return state.toList()
    }
}