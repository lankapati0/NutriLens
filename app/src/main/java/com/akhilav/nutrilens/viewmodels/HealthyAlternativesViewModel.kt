package com.akhilav.nutrilens.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akhilav.nutrilens.data.local.entity.ProductEntity
import com.akhilav.nutrilens.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlin.collections.emptyList

@HiltViewModel
class HealthyAlternativesViewModel @Inject constructor(
    private val repository: ProductRepository
): ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val searchResults: StateFlow<List<ProductEntity>> =
        _searchQuery
            .debounce(300)
            .flatMapLatest { query ->
                if (query.isBlank()) {
                    flowOf(emptyList())
                } else {
                    flow {
                        val localResults =
                            repository.searchProduct(query.trim()).first()

                        emit(localResults)

                        val onlineResults =
                            repository.searchOnlineProducts(query.trim())

                        emit(
                            (localResults + onlineResults)
                                .distinctBy { it.barcode }
                        )
                    }
                }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    private val _selectedProduct =
        MutableStateFlow<ProductEntity?>(null)

    val selectedProduct: StateFlow<ProductEntity?> = _selectedProduct

    val alternatives: StateFlow<List<ProductEntity>> =
        _selectedProduct.flatMapLatest { product ->
            val category = product?.categories?.split(",")?.firstOrNull()?.trim()

            if(category.isNullOrBlank() || product == null){
                flowOf(emptyList())
            } else {
                repository.getProductsByCategory(category, product.barcode)
            }
        }.map { products ->
            products.sortedByDescending { calculateNutritionScore(it) ?: -1 }
        } .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )



    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectProduct(product: ProductEntity) {
        _selectedProduct.value = product
        _searchQuery.value = ""
    }

     fun calculateNutritionScore(product: ProductEntity):Int?{
        val sugar = product.sugars
        val protein = product.proteins
        val fiber = product.fiber

        if (sugar == null || protein == null || fiber == null) {
            return null
        }

        var score = 0
        if (sugar != null) {
            score += if (sugar <= 5) 3 else if (sugar <= 10) 2 else if (sugar <= 22.5) 1 else 0
        }

        if (protein != null) {
            score += if (protein >= 10) 3 else if (protein >= 5) 2 else 1
        }

        if (fiber != null) {
            score += if (fiber >= 6) 3 else if (fiber >= 3) 2 else 1
        }

        return score
    }


}