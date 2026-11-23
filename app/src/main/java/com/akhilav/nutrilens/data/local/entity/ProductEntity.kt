package com.akhilav.nutrilens.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey
    val barcode: String,
    val productName: String?,
    val brands: String?,
    val categories: String?,
    @ColumnInfo(defaultValue = "0")
    val isFavorite:Boolean = false,
    val imageUrl: String?,

    val energyKcal: Double?,
    val proteins: Double?,
    val carbohydrates: Double?,
    val fat: Double?,
    val sugars: Double?,
    val fiber: Double?,
    val salt: Double?,
    val vitaminA: Double? = null,
    val vitaminC: Double? = null,
    val vitaminD: Double? = null,
    val vitaminB12: Double? = null,
    val calcium: Double? = null,
    val magnesium: Double? = null,
    val potassium: Double? = null,
    val zinc: Double? = null,
    val iron: Double? = null,
    val ingredientsText: String?,
    val nutritionGrade: String?,
    val novaGroup: Int?,
    val fatLevel: String? = null,
    val saturatedFatLevel: String? = null,
    val sugarLevel: String? = null,
    val saltLevel: String? = null,

    val additivesCount: Int? = null,
    val additivesTags: String? = null,
    val allergens: String? = null,
    val allergensTags: String? = null,
    val tracesTags: String? = null,
    val ingredientsAnalysisTags: String? = null,
    val labelsTags: String? = null,
    val ecoScoreGrade: String? = null,
    val lastUpdated: Long = System.currentTimeMillis()

)