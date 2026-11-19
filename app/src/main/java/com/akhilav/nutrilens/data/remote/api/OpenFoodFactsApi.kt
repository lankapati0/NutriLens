package com.akhilav.nutrilens.data.remote.api

import com.akhilav.nutrilens.data.remote.dto.ProductResponse
import com.akhilav.nutrilens.data.remote.dto.ProductSearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface OpenFoodFactsApi {

    @GET("api/v2/product/{barcode}.json")
    suspend fun getProduct(@Path("barcode") barcode: String): Response<ProductResponse>

    @GET("api/v2/search")
    suspend fun searchProducts(@Query("search_terms") query: String,
                               @Query("fields") fields: String = "code,product_name,brands,categories,image_url,nutriments,ingredients_text,nutrition_grade_fr,nova_group",
                               @Query("page_size") pageSize: Int = 20): Response<ProductSearchResponse>


}