package com.akhilav.nutrilens.viewmodels.micro_nutrient

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akhilav.nutrilens.data.local.dao.DailyMicronutritionSummary
import com.akhilav.nutrilens.data.repository.FoodConsumptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
@HiltViewModel
class MicronutrientViewModel @Inject constructor(
    private val repository: FoodConsumptionRepository
): ViewModel(){

    val todayMicronutrients = repository.getTodayMicronutrientSummary().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), DailyMicronutritionSummary(
            vitaminA = null,
            vitaminC = null,
            vitaminD = null, vitaminB12 = null,
            calcium = null, magnesium = null, potassium = null, iron = null, zinc = null
        )
    )
}