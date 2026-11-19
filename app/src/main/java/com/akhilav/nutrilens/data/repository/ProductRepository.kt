package com.akhilav.nutrilens.data.repository

import com.akhilav.nutrilens.data.local.dao.ProductDao
import com.akhilav.nutrilens.data.local.entity.ProductEntity
import com.akhilav.nutrilens.data.remote.api.OpenFoodFactsApi
import com.akhilav.nutrilens.data.remote.dto.ProductDto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlin.collections.emptyList

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
        categories = categories,
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

    fun searchProduct(query: String): Flow<List<ProductEntity>>{
        return productDao.searchProducts(query)
    }

    fun getProductsByCategory(category:String, excludedBarcode:String): Flow<List<ProductEntity>>{
        return productDao.getProductsbyCategory(category,excludedBarcode)
    }

    suspend fun searchOnlineProducts(query:String): List<ProductEntity>{
        return try {
            val response = api.searchProducts(query)

            if(response.isSuccessful){
                val products = response.body()?.products.orEmpty()
                val entities = products.mapNotNull { dto ->
                    val barcode = dto.barcode?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
                    dto.toEntity(barcode)
                }
                entities.forEach { product ->
                    productDao.insertProduct(product)
                }
                entities
            } else {
                emptyList()
            }
        } catch (e: Exception){
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun updateFavorite(barcode: String,isFavorite: Boolean){
        productDao.updateFavorite(barcode,isFavorite)
    }

    fun getFavoriteProducts(): Flow<List<ProductEntity>>{
        return productDao.getFavoriteProduct()
    }
}