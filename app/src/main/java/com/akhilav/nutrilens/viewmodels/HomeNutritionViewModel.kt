package com.akhilav.nutrilens.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akhilav.nutrilens.data.local.dao.DailyNutritionSummary
import com.akhilav.nutrilens.data.local.datastore.NutritionPreferences
import com.akhilav.nutrilens.data.local.entity.FoodConsumptionEntity
import com.akhilav.nutrilens.data.repository.FoodConsumptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.emptyList

@HiltViewModel
class HomeNutritionViewModel @Inject constructor(
    private val repository: FoodConsumptionRepository,
    private val nutritionPreferences: NutritionPreferences
): ViewModel(){
    init {
        Log.d("NutriLensFlow", "HomeNutritionViewModel created")
    }

    val dailyCaloriesTarget: StateFlow<Double> =
        nutritionPreferences.dailyCalorieTarget.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 2000.0
        )

    val dailyProteinTarget: StateFlow<Double> =
        nutritionPreferences.dailyProteinTarget.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 50.0
        )

    val dailyCarbsTarget: StateFlow<Double> =
        nutritionPreferences.dailyCarbsTarget.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 275.0
        )

    val dailyFatTarget: StateFlow<Double> =
        nutritionPreferences.dailyFatTarget.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 78.0
        )

    fun saveDailyCalorieTarget(target: Double){
        viewModelScope.launch {
            nutritionPreferences.saveDailyCalorieTarget(target)
        }
    }

    fun saveDailyProteinTarget(target: Double) {
        viewModelScope.launch {
            nutritionPreferences.saveDailyProteinTarget(target)
        }
    }

    fun saveDailyCarbsTarget(target: Double) {
        viewModelScope.launch {
            nutritionPreferences.saveDailyCarbsTarget(target)
        }
    }

    fun saveDailyFatTarget(target: Double) {
        viewModelScope.launch {
            nutritionPreferences.saveDailyFatTarget(target)
        }
    }

    val nutritionSummary: StateFlow<DailyNutritionSummary?> = repository.getTodayNutritionSummary()
        .onEach { summary ->
            Log.d("NutriLensFlow", "Nutrition summary: $summary")

        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val todaysConsumption: StateFlow<List<FoodConsumptionEntity>> = repository.getTodayConsumptions()
        .stateIn(scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList())

}