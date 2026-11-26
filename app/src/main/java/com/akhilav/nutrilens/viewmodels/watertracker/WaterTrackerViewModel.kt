package com.akhilav.nutrilens.viewmodels.watertracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akhilav.nutrilens.data.local.datastore.NutritionPreferences
import com.akhilav.nutrilens.data.local.entity.WaterIntakeEntity
import com.akhilav.nutrilens.data.repository.WaterIntakeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WaterTrackerViewModel @Inject constructor(
    private val repository: WaterIntakeRepository,
    private val nutritionPreferences: NutritionPreferences
): ViewModel(){

    val todayWaterIntake = repository.getTodayWaterIntake().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val weeklyWaterIntake = repository.getWeeklyWaterIntake().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val todayTotalWater: StateFlow<Int> =
        repository.getTodayTotalWater()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = 0
            )

    val dailyWaterTarget = nutritionPreferences.dailyWaterTarget.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        2000
    )

    fun addWater(amountMl: Int){
        if(amountMl<=0) return
        viewModelScope.launch {
            repository.addWater(amountMl)
        }
    }
    fun deleteWaterIntake(intake: WaterIntakeEntity){
        viewModelScope.launch {
            repository.deleteWaterIntake(intake)
        }
    }

    fun saveDailyWaterTarget(target:Int){
        if(target<=0) return
        viewModelScope.launch {
            nutritionPreferences.saveDailyWaterTarget(target)
        }
    }

}