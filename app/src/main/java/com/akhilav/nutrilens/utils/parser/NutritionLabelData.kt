package com.akhilav.nutrilens.utils.parser

data class NutritionLabelData(val nutrient: String,
                              val perServing: String? = null,
                              val per100g: String? = null,
                              val rda: String? = null)


data class IngredientLabelData(val ingredientsText:String,
    val containsStatement:String? = null,
    val mayContainStatement: String? = null)