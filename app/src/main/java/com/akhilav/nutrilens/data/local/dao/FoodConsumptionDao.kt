package com.akhilav.nutrilens.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.akhilav.nutrilens.data.local.entity.FoodConsumptionEntity
import kotlinx.coroutines.flow.Flow
import kotlin.Double

data class DailyNutritionSummary(
    val calories: Double?,
    val protein: Double?,
    val carbohydrates: Double?,
    val fat: Double?
)
data class DailyMicronutritionSummary(
    val vitaminA: Double?,
    val vitaminC: Double?,
    val vitaminD: Double?,
    val vitaminB12: Double?,
    val calcium: Double?,
    val magnesium: Double?,
    val potassium: Double?,
    val iron: Double?,
    val zinc: Double?
)
@Dao
interface FoodConsumptionDao {

    @Insert
    suspend fun insertConsumption(consumption: FoodConsumptionEntity)

    @Query("""SELECT CASE WHEN COUNT(*) = COUNT(calories) THEN SUM(calories) ELSE NULL END AS calories, CASE WHEN COUNT(*) = COUNT(protein) THEN SUM(protein) ELSE NULL END AS protein, CASE WHEN COUNT(*) = COUNT(carbohydrates) THEN SUM(carbohydrates) ELSE NULL END AS carbohydrates,CASE WHEN COUNT(*) = COUNT(fat) THEN SUM(fat) ELSE NULL END AS fat FROM food_consumption WHERE consumedAt >= :startTime AND consumedAt < :endTime""")
    fun getDailyNutritionSummary(
        startTime: Long,
        endTime: Long
    ): Flow<DailyNutritionSummary>

    @Query("""SELECT * FROM food_consumption WHERE consumedAt >= :startTime AND consumedAt < :endTime ORDER BY consumedAt DESC""")
    fun getTodayConsumptions(startTime: Long,endTime: Long): Flow<List<FoodConsumptionEntity>>

    @Query("""SELECT * FROM food_consumption WHERE consumedAt >= :startTime AND consumedAt < :endTime ORDER BY consumedAt ASC""")
    fun getConsumptionsBetween(startTime: Long,endTime: Long): Flow<List<FoodConsumptionEntity>>

    @Query("""SELECT * FROM food_consumption WHERE consumedAt >= :startTime AND consumedAt < :endTime AND mealType = :mealType ORDER BY consumedAt DESC""")
    fun getConsumptionsByMeal(startTime: Long,endTime: Long,mealType: String): Flow<List<FoodConsumptionEntity>>

    @Query("""
    SELECT
        SUM(vitaminA) AS vitaminA, SUM(vitaminC) AS vitaminC, SUM(vitaminD) AS vitaminD, SUM(vitaminB12) AS vitaminB12, SUM(calcium) AS calcium, SUM(iron) AS iron, SUM(potassium) AS potassium, SUM(magnesium) AS magnesium, SUM(zinc) AS zinc FROM food_consumption WHERE consumedAt >= :startTime AND consumedAt < :endTime""")
    fun getDailyMicronutrientSummary(startTime: Long,endTime: Long): Flow<DailyMicronutritionSummary>
}