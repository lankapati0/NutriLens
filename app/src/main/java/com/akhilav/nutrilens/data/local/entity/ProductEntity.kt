package com.akhilav.nutrilens.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey
    val barcode: String,
    val productName: String?,
    val brands: String?,
    val imageUrl: String?,

    val energyKcal: Double?,
    val proteins: Double?,
    val carbohydrates: Double?,
    val fat: Double?,
    val sugars: Double?,
    val fiber: Double?,
    val salt: Double?,

    val ingredientsText: String?,
    val nutritionGrade: String?,
    val novaGroup: Int?,
    val lastUpdated: Long = System.currentTimeMillis()

)