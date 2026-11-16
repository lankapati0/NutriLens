package com.akhilav.nutrilens.data.repository

import com.akhilav.nutrilens.data.local.dao.ProductDao
import com.akhilav.nutrilens.data.local.entity.ProductEntity
import com.akhilav.nutrilens.data.remote.api.OpenFoodFactsApi
import com.akhilav.nutrilens.data.remote.dto.ProductDto
import javax.inject.Inject

class ProductRepository @Inject constructor(
    private val productDao: ProductDao,
    private val api: OpenFoodFactsApi
) {
    suspend fun getProduct(barcode: String): ProductEntity? {

        // Check local database first
        val cachedProduct = productDao.getProductByBarcode(barcode)

        if (cachedProduct != null) {
            return cachedProduct
        }

        return try {
            val response = api.getProduct(barcode)
            if (response.isSuccessful) {
                val body = response.body()

                if (body?.status == 1 && body.product != null) {
                    val product = body.product.toEntity(barcode)
                    productDao.insertProduct(product)
                    product
                } else {
                    null
                }
            } else {
                null

            }
        } catch (e: Exception) {
            null
        }
    }

    private fun ProductDto.toEntity(barcode: String) = ProductEntity(
        barcode = barcode,
        productName = productName,
        brands = brands,
        imageUrl = imageUrl,
        energyKcal = nutriments?.energyKcal,
        proteins = nutriments?.proteins,
        carbohydrates = nutriments?.carbohydrates,
        fat = nutriments?.fat,
        sugars = nutriments?.sugars,
        fiber = nutriments?.fiber,
        salt = nutriments?.salt,
        ingredientsText = ingredientsText,
        nutritionGrade = nutritionGrade,
        novaGroup = novaGroup
    )
}