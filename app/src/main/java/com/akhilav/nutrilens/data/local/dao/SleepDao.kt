package com.akhilav.nutrilens.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.akhilav.nutrilens.data.local.entity.SleepEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SleepDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSleepRecord(sleep: SleepEntity)

    @Update
    suspend fun updateSleepRecord(sleep: SleepEntity)

    @Query("SELECT * FROM sleep_records ORDER BY date DESC")
    fun getAllSleepRecords(): Flow<List<SleepEntity>>

    @Query("SELECT * FROM sleep_records WHERE date = :date LIMIT 1")
    suspend fun getSleepRecordByDate(date:String): SleepEntity?

    @Query("DELETE FROM sleep_records WHERE id = :sleepId")
    suspend fun deleteSleepRecord(sleepId: Int)

    @Query("DELETE FROM sleep_records")
    suspend fun deleteAllSleepRecords()
}