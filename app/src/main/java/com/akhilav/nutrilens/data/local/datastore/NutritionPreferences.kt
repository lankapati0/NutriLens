package com.akhilav.nutrilens.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
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
        val MEAL_REMINDERS_ENABLED = booleanPreferencesKey("meal_reminders_enabled")
        val MEAL_REMINDER_HOUR = intPreferencesKey("meal_reminder_hour")
        val MEAL_REMINDER_MINUTE = intPreferencesKey("meal_reminder_minute")
        val WATER_REMINDERS_ENABLED = booleanPreferencesKey("water_reminders_enabled")

        val WATER_REMINDER_HOUR = intPreferencesKey("water_reminder_hour")

        val WATER_REMINDER_MINUTE = intPreferencesKey("water_reminder_minute")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val VEGAN_ENABLED = booleanPreferencesKey("vegan_enabled")
        val VEGETARIAN_ENABLED = booleanPreferencesKey("vegetarian_enabled")
        val GLUTEN_FREE_ENABLED = booleanPreferencesKey("gluten_free_enabled")
        val DAIRY_FREE_ENABLED = booleanPreferencesKey("dairy_free_enabled")
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

    val waterRemindersEnabled:Flow<Boolean> =
        context.nutritionDataStore.data.map { preferences ->
            preferences[WATER_REMINDERS_ENABLED] ?: false
    }

    val waterReminderHour: Flow<Int> =
        context.nutritionDataStore.data.map { preferences ->
            preferences[WATER_REMINDER_HOUR] ?: 10
    }

    val waterReminderMinute: Flow<Int> =
        context.nutritionDataStore.data.map { preferences ->
            preferences[WATER_REMINDER_MINUTE] ?: 0
    }

    val mealRemindersEnabled: Flow<Boolean> =
        context.nutritionDataStore.data.map { preferences ->
            preferences[MEAL_REMINDERS_ENABLED] ?: false
    }

    val mealReminderHour: Flow<Int> =
        context.nutritionDataStore.data.map { preferences ->
            preferences[MEAL_REMINDER_HOUR] ?: 13
    }

    val mealReminderMinute: Flow<Int> =
        context.nutritionDataStore.data.map { preferences ->
            preferences[MEAL_REMINDER_MINUTE] ?: 0
    }

    val notificationsEnabled: Flow<Boolean> =
        context.nutritionDataStore.data.map { preferences ->
            preferences[NOTIFICATIONS_ENABLED] ?: true
    }

    val veganEnabled: Flow<Boolean> =
        context.nutritionDataStore.data.map { preferences ->
            preferences[VEGAN_ENABLED] ?: true
    }

    val vegetarianEnabled: Flow<Boolean> =
        context.nutritionDataStore.data.map { preferences ->
            preferences[VEGETARIAN_ENABLED] ?: true
    }

    val glutenFreeEnabled: Flow<Boolean> =
        context.nutritionDataStore.data.map { preferences ->
            preferences[GLUTEN_FREE_ENABLED] ?: true
    }

    val dairyFreeEnabled: Flow<Boolean> =
        context.nutritionDataStore.data.map { preferences ->
            preferences[DAIRY_FREE_ENABLED] ?: true
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

    suspend fun saveMealRemindersEnabled(enabled:Boolean){
        context.nutritionDataStore.edit { preference ->
            preference[MEAL_REMINDERS_ENABLED] = enabled
        }
    }

    suspend fun saveMealReminderTime(hour: Int, minute: Int) {
        context.nutritionDataStore.edit { preferences ->
            preferences[MEAL_REMINDER_HOUR] = hour
            preferences[MEAL_REMINDER_MINUTE] = minute
        }
    }

    suspend fun saveWaterRemindersEnabled(enabled: Boolean) {
        context.nutritionDataStore.edit { preferences ->
            preferences[WATER_REMINDERS_ENABLED] = enabled
        }
    }

    suspend fun saveWaterReminderTime(hour: Int, minute: Int) {
        context.nutritionDataStore.edit { preferences ->
            preferences[WATER_REMINDER_HOUR] = hour
            preferences[WATER_REMINDER_MINUTE] = minute
        }
    }

    suspend fun saveNotificationEnabled(enabled: Boolean){
        context.nutritionDataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun saveVeganEnabled(enabled: Boolean){
        context.nutritionDataStore.edit { preferences ->
            preferences[VEGAN_ENABLED] = enabled
        }
    }

    suspend fun saveVegetarianEnabled(enabled: Boolean){
        context.nutritionDataStore.edit { preferences ->
            preferences[VEGETARIAN_ENABLED] = enabled
        }
    }

    suspend fun saveGlutenFreeEnabled(enabled: Boolean){
        context.nutritionDataStore.edit { preferences ->
            preferences[GLUTEN_FREE_ENABLED] = enabled
        }
    }

    suspend fun saveDairyFreeEnabled(enabled: Boolean){
        context.nutritionDataStore.edit { preferences ->
            preferences[DAIRY_FREE_ENABLED] = enabled
        }
    }

}