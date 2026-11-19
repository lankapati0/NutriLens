package com.akhilav.nutrilens.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.nutritionDataStore by preferencesDataStore(
    name = "nutrition_preferences"
)
@Singleton
class NutritionPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object{
        val DAILY_CALORIE_TARGET = doublePreferencesKey("daily_calorie_target")
        val DAILY_PROTEIN_TARGET = doublePreferencesKey("daily_protein_target")
        val DAILY_CARBS_TARGET = doublePreferencesKey("daily_carbs_target")
        val DAILY_FAT_TARGET = doublePreferencesKey("daily_fat_target")
        val DAILY_WATER_TARGET = intPreferencesKey("daily_water_target")
    }

    val dailyCalorieTarget: Flow<Double> = context.nutritionDataStore.data.map { preferences ->
        preferences[DAILY_CALORIE_TARGET] ?: 2000.0
    }

    val dailyProteinTarget: Flow<Double> = context.nutritionDataStore.data.map { preferences ->
        preferences[DAILY_PROTEIN_TARGET] ?: 50.0
    }
    val dailyCarbsTarget: Flow<Double> =
        context.nutritionDataStore.data.map { preferences ->
            preferences[DAILY_CARBS_TARGET] ?: 275.0
    }

    val dailyFatTarget: Flow<Double> =
        context.nutritionDataStore.data.map { preferences ->
            preferences[DAILY_FAT_TARGET] ?: 78.0
    }

    val dailyWaterTarget:Flow<Int> =
        context.nutritionDataStore.data.map { preferences ->
            preferences[DAILY_WATER_TARGET] ?: 2000
    }

    suspend fun saveDailyCalorieTarget(target: Double){
        context.nutritionDataStore.edit { preferences ->
            preferences[DAILY_CALORIE_TARGET] = target
        }
    }
    suspend fun saveDailyProteinTarget(target: Double) {
        context.nutritionDataStore.edit { preferences ->
            preferences[DAILY_PROTEIN_TARGET] = target
        }
    }

    suspend fun saveDailyCarbsTarget(target: Double) {
        context.nutritionDataStore.edit { preferences ->
            preferences[DAILY_CARBS_TARGET] = target
        }
    }

    suspend fun saveDailyFatTarget(target: Double) {
        context.nutritionDataStore.edit { preferences ->
            preferences[DAILY_FAT_TARGET] = target
        }
    }

    suspend fun saveDailyWaterTarget(target:Int){
        context.nutritionDataStore.edit { preferences ->
            preferences[DAILY_WATER_TARGET] = target
        }
    }
}