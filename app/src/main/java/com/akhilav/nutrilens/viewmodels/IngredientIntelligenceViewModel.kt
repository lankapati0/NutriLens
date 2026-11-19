package com.akhilav.nutrilens.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akhilav.nutrilens.data.local.entity.ProductEntity
import com.akhilav.nutrilens.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlin.collections.emptyList

@OptIn(FlowPreview::class)
@HiltViewModel
class IngredientIntelligenceViewModel @Inject constructor(
    private val repository: ProductRepository
): ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val searchResults: StateFlow<List<ProductEntity>> =
        _searchQuery.debounce(300).flatMapLatest { query ->
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

    private val _selectedProduct = MutableStateFlow<ProductEntity?>(null)
    val selectedProduct: MutableStateFlow<ProductEntity?> = _selectedProduct

    fun updateSearchQuery(query: String){
        _searchQuery.value = query
    }

    fun selectProduct(product: ProductEntity){
        _selectedProduct.value = product
        _searchQuery.value = ""
    }
}