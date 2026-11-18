package com.akhilav.nutrilens.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.akhilav.nutrilens.data.local.entity.FoodConsumptionEntity
import kotlinx.coroutines.flow.Flow

data class DailyNutritionSummary(
    val calories: Double?,
    val protein: Double?,
    val carbohydrates: Double?,
    val fat: Double?
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

}