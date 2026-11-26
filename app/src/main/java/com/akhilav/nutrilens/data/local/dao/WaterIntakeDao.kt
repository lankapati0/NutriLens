package com.akhilav.nutrilens.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.akhilav.nutrilens.data.local.entity.WaterIntakeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WaterIntakeDao {

    @Insert
    suspend fun insertWaterIntake(intake: WaterIntakeEntity)

    @Delete
    suspend fun deleteWaterIntake(intake: WaterIntakeEntity)
    @Query("""SELECT * FROM water_intake WHERE consumedAt >= :startTime AND consumedAt < :endTime ORDER BY consumedAt DESC """)
    fun getTodayWaterIntake(startTime:Long, endTime: Long): Flow<List<WaterIntakeEntity>>

    @Query("""SELECT COALESCE(SUM(amountMl), 0) FROM water_intake WHERE consumedAt >= :startTime AND consumedAt < :endTime""")
    fun getTodayTotalWater(startTime: Long, endTime: Long): Flow<Int>

    @Query("""SELECT * FROM water_intake WHERE consumedAt >= :startTime AND consumedAt < :endTime ORDER BY consumedAt ASC""")
    fun getWaterIntakeBetween(startTime: Long,endTime: Long): Flow<List<WaterIntakeEntity>>
}
