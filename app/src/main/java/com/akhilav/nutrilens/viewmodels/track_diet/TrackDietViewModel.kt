package com.akhilav.nutrilens.viewmodels.track_diet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akhilav.nutrilens.data.local.dao.DailyNutritionSummary
import com.akhilav.nutrilens.data.local.datastore.NutritionPreferences
import com.akhilav.nutrilens.data.local.entity.FoodConsumptionEntity
import com.akhilav.nutrilens.data.repository.FoodConsumptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

data class DailyNutritionPoint(
    val date: LocalDate, val calories: Double?,
    val protein: Double?, val carbohydrates: Double?, val fat: Double?
)

@HiltViewModel
class TrackDietViewModel @Inject constructor(
    private val repository: FoodConsumptionRepository,
    private val nutritionPreferences: NutritionPreferences
): ViewModel(){
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate
    private val _selectedMetric = MutableStateFlow("Calories")
    val selectedMetric: StateFlow<String> = _selectedMetric
    private val _selectedRange = MutableStateFlow("Week")
    val selectedRange: StateFlow<String> = _selectedRange


    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedDateNutrition: StateFlow<DailyNutritionSummary?> =
        selectedDate.flatMapLatest { date ->
            repository.getNutritionSummaryForDate(date)
        }.stateIn(scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null)

    val selectedDateConsumption: StateFlow<List<FoodConsumptionEntity>> =
        selectedDate.flatMapLatest { date ->
            repository.getConsumptionsForDate(date)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
    )

    val dailyProteinTarget: StateFlow<Double> = nutritionPreferences.dailyProteinTarget.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000),50.0
    )
    val dailyCaloriesTarget: StateFlow<Double> =
        nutritionPreferences.dailyCalorieTarget.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 2000.0
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

    val nutritionSummary: StateFlow<DailyNutritionSummary?> =
        repository.getTodayNutritionSummary().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
    )
    val todaysConsumption: StateFlow<List<FoodConsumptionEntity>> =
        repository.getTodayConsumptions().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
    )
    val weeklyConsumption: StateFlow<List<FoodConsumptionEntity>> =
        repository.getConsumptionsForLastDays(7).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
    )

    val monthlyConsumption: StateFlow<List<FoodConsumptionEntity>> =
        repository.getConsumptionsForLastDays(30).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
    )

    val weeklyNutritionPoints: StateFlow<List<DailyNutritionPoint>> =
        weeklyConsumption.map { entries ->
            val zone = ZoneId.systemDefault()
            val today = LocalDate.now(zone)

            (6 downTo 0).map { daysAgo ->
                val date = today.minusDays(daysAgo.toLong())

                val dayEntries = entries.filter { Instant.ofEpochMilli(it.consumedAt).atZone(zone).toLocalDate() == date }

                fun total(value:(FoodConsumptionEntity) -> Double?): Double? {
                    if(dayEntries.isEmpty()) return null
                    if(dayEntries.any { value(it) == null }) return null
                    return dayEntries.sumOf { value(it)!! }
                }

                DailyNutritionPoint(date, total{it.calories}, total{it.protein},
                    total { it.carbohydrates }, total { it.fat })
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList())

    val monthlyNutritionPoints: StateFlow<List<DailyNutritionPoint>> =
        monthlyConsumption.map { entries ->
            val zone = ZoneId.systemDefault()
            val today = LocalDate.now(zone)

            (29 downTo 0).map { daysAgo ->
                val date = today.minusDays(daysAgo.toLong())

                val dayEntries = entries.filter {
                    Instant.ofEpochMilli(it.consumedAt)
                        .atZone(zone)
                        .toLocalDate() == date
                }

                fun total(value: (FoodConsumptionEntity) -> Double?): Double? {
                    if (dayEntries.isEmpty()) return null
                    if (dayEntries.any { value(it) == null }) return null
                    return dayEntries.sumOf { value(it)!! }
                }

                DailyNutritionPoint(
                    date = date,
                    calories = total { it.calories },
                    protein = total { it.protein },
                    carbohydrates = total { it.carbohydrates },
                    fat = total { it.fat }
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
    )

    fun selectMetric(metric: String){
        _selectedMetric.value = metric
    }

    fun selectDate(date: LocalDate){
        _selectedDate.value = date
    }

    fun selectRange(range:String){
        _selectedRange.value = range
    }

}