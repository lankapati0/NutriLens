package com.akhilav.nutrilens.viewmodels.weightEntry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akhilav.nutrilens.data.repository.WeightRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeightViewModel @Inject constructor(
    private val repository: WeightRepository
): ViewModel() {
    val weightHistory = repository
        .getWeightHistory()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val latestWeight = repository
        .getLatestWeight()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

    fun saveWeight(weightKg: Double){
        if(weightKg <=0) return
        viewModelScope.launch {
            repository.saveWeight(weightKg)
        }
    }


}