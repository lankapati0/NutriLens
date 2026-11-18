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
    val consumedAt: Long = System.currentTimeMillis()
)