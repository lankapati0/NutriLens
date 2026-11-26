package com.akhilav.nutrilens.data.repository

import com.akhilav.nutrilens.data.local.dao.WaterIntakeDao
import com.akhilav.nutrilens.data.local.entity.WaterIntakeEntity
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WaterIntakeRepository @Inject constructor(
    private val waterIntakeDao: WaterIntakeDao
){
    private fun getTodayRange(): Pair<Long,Long> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY,0)
        calendar.set(Calendar.MINUTE,0)
        calendar.set(Calendar.SECOND,0)
        calendar.set(Calendar.MILLISECOND,0)
        val startTime = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_YEAR,1)
        val endTime = calendar.timeInMillis
        return Pair(startTime,endTime)
    }

    fun getTodayWaterIntake(): Flow<List<WaterIntakeEntity>> {
        val (startTime,endTime) = getTodayRange()
        return waterIntakeDao.getTodayWaterIntake(startTime,endTime)
    }

    suspend fun addWater(amountMl:Int){
        waterIntakeDao.insertWaterIntake(WaterIntakeEntity(amountMl = amountMl))
    }

    fun getTodayTotalWater(): Flow<Int>{
        val (startTime,endTime) = getTodayRange()
        return waterIntakeDao.getTodayTotalWater(startTime, endTime )
    }

    suspend fun deleteWaterIntake(intake: WaterIntakeEntity){
        waterIntakeDao.deleteWaterIntake(intake)
    }

    private fun getLast7DaysRange():Pair<Long,Long> {
        val calendar = Calendar.getInstance()
        // End time: tomorrow at midnight
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val endTime = calendar.timeInMillis

        // Start time: midnight, six days before today
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val startTime = calendar.timeInMillis

        return Pair(startTime,endTime)
    }

    fun getWeeklyWaterIntake(): Flow<List<WaterIntakeEntity>> {
        val (startTime, endTime) = getLast7DaysRange()
        return waterIntakeDao.getWaterIntakeBetween(startTime,endTime)
    }

}