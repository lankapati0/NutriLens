package com.akhilav.nutrilens.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ProductResponse(
    @SerializedName("status")
    val status:Int?,
    @SerializedName("product")
    val product: ProductDto?
)

data class NutrimentsDto(
    @SerializedName("energy-kcal_100g")
    val energyKcal: Double?,

    @SerializedName("proteins_100g")
    val proteins: Double?,

     @SerializedName("carbohydrates_100g")
     val carbohydrates: Double?,

     @SerializedName("fat_100g")
     val fat: Double?,

     @SerializedName("sugars_100g")
     val sugars: Double?,

     @SerializedName("fiber_100g")
     val fiber: Double?,

    @SerializedName("salt_100g")
    val salt: Double?,

    @SerializedName("vitamin-a_100g")
    val vitaminA: Double?,

    @SerializedName("vitamin-c_100g")
    val vitaminC: Double?,

    @SerializedName("vitamin-d_100g")
    val vitaminD: Double?,

    @SerializedName("vitamin-b12_100g")
    val vitaminB12: Double?,

    @SerializedName("calcium_100g")
    val calcium: Double?,

    @SerializedName("iron_100g")
    val iron: Double?,

    @SerializedName("potassium_100g")
    val potassium: Double?,

    @SerializedName("magnesium_100g")
    val magnesium: Double?,

    @SerializedName("zinc_100g")
    val zinc: Double?
)
data class ProductDto(
    @SerializedName("code")
    val barcode: String?,

    @SerializedName("product_name")
    val productName: String?,

    @SerializedName("brands")
    val brands: String?,
    @SerializedName("categories")
    val categories: String?,
    @SerializedName("image_url")
    val imageUrl: String?,

    @SerializedName("nutriments")
    val nutriments: NutrimentsDto?,

    @SerializedName("ingredients_text")
    val ingredientsText: String?,

    @SerializedName("nutrition_grade_fr")
    val nutritionGrade: String?,

    @SerializedName("nova_group")
    val novaGroup: Int?,
    @SerializedName("nutrient_levels")
    val nutrientLevels:NutrientLevelDto?,
    @SerializedName("additives_n")
    val additivesCount:Int?,
    @SerializedName("additives_tags")
    val additivesTags: List<String>?,

    @SerializedName("allergens")
    val allergens: String?,

    @SerializedName("allergens_tags")
    val allergensTags: List<String>?,

    @SerializedName("traces_tags")
    val tracesTags: List<String>?,
    @SerializedName("ingredients_analysis_tags")
    val ingredientsAnalysisTags: List<String>?,

    @SerializedName("labels_tags")
    val labelsTags: List<String>?,

    @SerializedName("ecoscore_grade")
    val ecoScoreGrade: String?
)
data class NutrientLevelDto(
    @SerializedName("fat")
    val fat:String?,
    @SerializedName("saturated-fat")
    val saturatedFat:String?,
    @SerializedName("sugars")
    val sugars: String?,
    @SerializedName("salt")
    val salt: String?
)

data class ProductSearchResponse(
    @SerializedName("products")
    val products:List<ProductDto>?
)