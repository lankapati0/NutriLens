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
     val salt: Double?
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
    val novaGroup: Int?
)

data class ProductSearchResponse(
    @SerializedName("products")
    val products:List<ProductDto>?
)