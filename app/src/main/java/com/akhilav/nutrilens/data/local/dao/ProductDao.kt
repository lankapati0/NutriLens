package com.akhilav.nutrilens.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.akhilav.nutrilens.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    @Query("SELECT * FROM products WHERE barcode = :barcode LIMIT 1")
    suspend fun getProductByBarcode(barcode: String): ProductEntity?

    @Query("SELECT * FROM products ORDER BY lastUpdated DESC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query(""" SELECT * FROM products WHERE productName LIKE '%' || :query || '%' ORDER BY productName ASC""")
    fun searchProducts(query:String): Flow<List<ProductEntity>>
    @Query("DELETE FROM products WHERE barcode = :barcode")
    suspend fun deleteProduct(barcode: String)

    @Query("DELETE FROM products")
    suspend fun clearProducts()

    @Query("""SELECT * FROM products WHERE barcode != :excludedBarcode AND categories IS NOT NULL AND categories LIKE '%' || :category || '%' ORDER BY productName ASC LIMIT 30""")
    fun getProductsbyCategory(category:String, excludedBarcode:String):Flow<List<ProductEntity>>

    @Query("UPDATE products SET isFavorite = :isFavorite WHERE barcode = :barcode")
    suspend fun updateFavorite(barcode:String, isFavorite: Boolean)

    @Query("SELECT * FROM products WHERE isFavorite = 1 ORDER BY lastUpdated DESC")
    fun getFavoriteProduct(): Flow<List<ProductEntity>>
}