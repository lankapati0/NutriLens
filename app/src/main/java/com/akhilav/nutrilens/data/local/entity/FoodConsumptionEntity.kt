package com.akhilav.nutrilens.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_consumption")
data class FoodConsumptionEntity(
    @PrimaryKey(autoGenerate = true)
    val id:Long = 0,
    val barcode:String? = null,
    val productName:String,
    val quantityGrams: Double,
    val calories: Double?,
    val protein: Double?,
    val carbohydrates: Double?,
    val fat: Double?,
    val vitaminA:Double?= null,
    val vitaminC: Double? = null,
    val vitaminD: Double? = null,
    val vitaminB12: Double? = null,
    val calcium: Double? = null,
    val iron: Double? = null,
    val potassium: Double? = null,
    val magnesium: Double? = null,
    val zinc: Double? = null,
    val mealType: String = "Other",
    val consumedAt: Long = System.currentTimeMillis()
)