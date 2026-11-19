package com.akhilav.nutrilens.viewmodels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akhilav.nutrilens.data.local.dao.ScanHistoryDao
import com.akhilav.nutrilens.data.local.entity.FoodConsumptionEntity
import com.akhilav.nutrilens.data.local.entity.ProductEntity
import com.akhilav.nutrilens.data.local.entity.ScanHistoryEntity
import com.akhilav.nutrilens.data.repository.FoodConsumptionRepository
import com.akhilav.nutrilens.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val foodConsumptionRepository: FoodConsumptionRepository,
    private val scanHistoryDao: ScanHistoryDao
): ViewModel(){
    private val _product = MutableStateFlow<ProductEntity?>(null)
    val product: StateFlow<ProductEntity?> = _product
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadProduct(barcode:String){
        viewModelScope.launch {
            _isLoading.value = true
            _product.value = null
            _error.value = null

            val result = productRepository.getProduct(barcode)
            if(result != null){
                _product.value = result
                try {
                    scanHistoryDao.insertScan(ScanHistoryEntity(
                        barcode = result.barcode,
                        productName = result.productName
                    ))
                }catch (e: Exception){
                    e.printStackTrace()
                }
            } else {
                _error.value = "Product not found or unable to fetch product details."
            }
            _isLoading.value = false
        }
    }

    fun saveConsumption(quantityGrams: Double){
        val currentProduct = _product.value ?: return
        viewModelScope.launch {
            try {
                foodConsumptionRepository.addConsumption(
                    FoodConsumptionEntity(
                        barcode = currentProduct.barcode,
                        productName = currentProduct.productName ?: "Unknown product",
                        quantityGrams = quantityGrams,
                        calories = currentProduct.energyKcal?.let { it * quantityGrams/100.0 },
                        protein = currentProduct.proteins?.let { it*quantityGrams/100.0 },
                        carbohydrates = currentProduct.carbohydrates?.let { it*quantityGrams/100.0 },
                        fat = currentProduct.fat?.let { it*quantityGrams/100.0 }
                    )
                )
            }catch (e: Exception){
                e.printStackTrace()
                _error.value = "Unable to save food consumption."
            }
        }
    }
}