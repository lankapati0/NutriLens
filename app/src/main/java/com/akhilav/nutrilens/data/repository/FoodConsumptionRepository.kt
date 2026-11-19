package com.akhilav.nutrilens.data.repository

import com.akhilav.nutrilens.data.local.dao.DailyNutritionSummary
import com.akhilav.nutrilens.data.local.dao.FoodConsumptionDao
import com.akhilav.nutrilens.data.local.entity.FoodConsumptionEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FoodConsumptionRepository @Inject constructor(
    private val foodConsumptionDao: FoodConsumptionDao
){
    suspend fun addConsumption(consumption: FoodConsumptionEntity){
        foodConsumptionDao.insertConsumption(consumption)
    }

    fun getTodayNutritionSummary(): Flow<DailyNutritionSummary> {
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone)
        val startTime = today.atStartOfDay(zone).toInstant().toEpochMilli()
        val endTime = today.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()

        return foodConsumptionDao.getDailyNutritionSummary(startTime, endTime)
    }

    fun getTodayConsumptions(): Flow<List<FoodConsumptionEntity>> {
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone)

        val startTime = today.atStartOfDay(zone)
            .toInstant().toEpochMilli()

        val endTime = today.plusDays(1).atStartOfDay(zone)
            .toInstant().toEpochMilli()

        return foodConsumptionDao.getTodayConsumptions(startTime, endTime)
    }

    fun getConsumptionsForLastDays(days:Long): Flow<List<FoodConsumptionEntity>> {
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone)

        val startTime = today.minusDays(days - 1).atStartOfDay(zone).toInstant().toEpochMilli()

        val endTime = today.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()

        return foodConsumptionDao.getTodayConsumptions(startTime,endTime)
    }

}