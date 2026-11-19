package com.akhilav.nutrilens.viewmodels.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akhilav.nutrilens.data.local.entity.ProductEntity
import com.akhilav.nutrilens.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class FavoritesViewModel @Inject constructor(
    val repository: ProductRepository
): ViewModel(){
    val favoriteProducts: StateFlow<List<ProductEntity>> =
        repository.getFavoriteProducts().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun updateFavorite(barcode: String, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.updateFavorite(barcode, isFavorite)
        }
    }
}