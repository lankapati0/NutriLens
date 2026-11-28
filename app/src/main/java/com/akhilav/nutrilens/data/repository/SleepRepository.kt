package com.akhilav.nutrilens.data.repository

import com.akhilav.nutrilens.data.local.dao.SleepDao
import com.akhilav.nutrilens.data.local.entity.SleepEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SleepRepository @Inject constructor(
    private val sleepDao: SleepDao
) {
    fun getAllSleepRecords(): Flow<List<SleepEntity>>{
        return sleepDao.getAllSleepRecords()
    }
    suspend fun insertSleepRecord(sleep: SleepEntity){
        sleepDao.insertSleepRecord(sleep)
    }

    suspend fun updateSleepRecord(sleep: SleepEntity){
        sleepDao.updateSleepRecord(sleep)
    }
    suspend fun getSleepRecordByDate(date:String): SleepEntity? {
        return sleepDao.getSleepRecordByDate(date)
    }
    suspend fun deleteAllSleepRecords(){
        sleepDao.deleteAllSleepRecords()
    }

    suspend fun deleteSleepRecord(sleepId:Int){
        sleepDao.deleteSleepRecord(sleepId)
    }
}