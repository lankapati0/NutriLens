package com.akhilav.nutrilens.data.remote.api

import com.akhilav.nutrilens.data.remote.dto.ProductResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface OpenFoodFactsApi {

    @GET("api/v2/product/{barcode}.json")
    suspend fun getProduct(@Path("barcode") barcode: String): Response<ProductResponse>


}