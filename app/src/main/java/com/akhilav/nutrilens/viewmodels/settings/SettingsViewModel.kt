package com.akhilav.nutrilens.viewmodels.settings

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akhilav.nutrilens.data.local.datastore.NutritionPreferences
import com.akhilav.nutrilens.data.worker.MealReminderScheduler
import com.akhilav.nutrilens.utils.WaterReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val nutritionPreferences: NutritionPreferences,
    @ApplicationContext private val context: Context
): ViewModel(){
    val dailyCalorieTarget: StateFlow<Double> =
        nutritionPreferences.dailyCalorieTarget.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            2000.0
        )

    val dailyProteinTarget: StateFlow<Double> =
        nutritionPreferences.dailyProteinTarget.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            50.0
        )

    val dailyCarbsTarget: StateFlow<Double> =
        nutritionPreferences.dailyCarbsTarget.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            275.0
        )

    val dailyFatTarget: StateFlow<Double> =
        nutritionPreferences.dailyFatTarget.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            78.0
        )

    val dailyWaterTarget: StateFlow<Int> =
        nutritionPreferences.dailyWaterTarget.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            2000
    )

    val mealRemindersEnabled = nutritionPreferences.mealRemindersEnabled
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            false
        )

    val mealReminderHour = nutritionPreferences.mealReminderHour
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            13
        )

    val mealReminderMinute = nutritionPreferences.mealReminderMinute
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            0
    )

    val waterRemindersEnabled = nutritionPreferences.waterRemindersEnabled.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), false
    )

    val waterReminderHour =
        nutritionPreferences.waterReminderHour.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), 10)

    val waterReminderMinute =
        nutritionPreferences.waterReminderMinute.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val notificationEnabled = nutritionPreferences.notificationsEnabled.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000),true)

    fun saveCalorieTarget(value: Double) {
        viewModelScope.launch {
            nutritionPreferences.saveDailyCalorieTarget(value)
        }
    }

    fun saveProteinTarget(value: Double) {
        viewModelScope.launch {
            nutritionPreferences.saveDailyProteinTarget(value)
        }
    }

    fun saveCarbsTarget(value: Double) {
        viewModelScope.launch {
            nutritionPreferences.saveDailyCarbsTarget(value)
        }
    }

    fun saveFatTarget(value: Double) {
        viewModelScope.launch {
            nutritionPreferences.saveDailyFatTarget(value)
        }
    }

    fun saveWaterTarget(value: Int) {
        viewModelScope.launch {
            nutritionPreferences.saveDailyWaterTarget(value)
        }
    }

    fun saveMealRemindersEnabled(enabled:Boolean){
        viewModelScope.launch {
            nutritionPreferences.saveMealRemindersEnabled(enabled)

            if(enabled){
                val hour = nutritionPreferences.mealReminderHour.first()
                val minute = nutritionPreferences.mealReminderMinute.first()
                MealReminderScheduler.schedule(context,hour,minute)
            } else {
                MealReminderScheduler.cancel(context)
            }
        }
    }

    fun saveMealRemindersTime(hour:Int, minute:Int){
        viewModelScope.launch {
            nutritionPreferences.saveMealReminderTime(hour, minute )
            val enabled = nutritionPreferences.mealRemindersEnabled.first()

            if(enabled){
                MealReminderScheduler.schedule(context,hour,minute)
            }
        }
    }

    fun saveWaterRemindersEnabled(enabled: Boolean){
        viewModelScope.launch {
            nutritionPreferences.saveWaterRemindersEnabled(enabled)

            if(enabled){
                val hour = nutritionPreferences.waterReminderHour.first()
                val minute = nutritionPreferences.waterReminderMinute.first()
                WaterReminderScheduler.schedule(context,hour,minute)
            } else {
                WaterReminderScheduler.cancel(context)
            }
        }
    }

    fun saveWaterReminderTime(hour: Int,minute: Int){
        viewModelScope.launch {
            nutritionPreferences.saveWaterReminderTime(hour,minute)

            val enabled = nutritionPreferences.waterRemindersEnabled.first()
            if(enabled){
                WaterReminderScheduler.schedule(context,hour,minute)
            }
        }
    }

    fun saveNotificationEnabled(enabled: Boolean){
        viewModelScope.launch {
            nutritionPreferences.saveNotificationEnabled(enabled)
            if(enabled){
                val mealEnabled = nutritionPreferences.mealRemindersEnabled.first()
                val waterEnabled = nutritionPreferences.waterRemindersEnabled.first()
                if(mealEnabled){
                    val hour = nutritionPreferences.mealReminderHour.first()
                    val minute = nutritionPreferences.mealReminderMinute.first()
                    MealReminderScheduler.schedule(context,hour,minute)
                }
                if(waterEnabled){
                    val hour = nutritionPreferences.waterReminderHour.first()
                    val minute = nutritionPreferences.waterReminderMinute.first()
                    WaterReminderScheduler.schedule(context,hour,minute)
                }
            } else {
                MealReminderScheduler.cancel(context)
                WaterReminderScheduler.cancel(context)
            }
        }
    }

}