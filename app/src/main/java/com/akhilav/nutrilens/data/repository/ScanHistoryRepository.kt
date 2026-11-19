package com.akhilav.nutrilens.data.repository

import com.akhilav.nutrilens.data.local.dao.ScanHistoryDao
import com.akhilav.nutrilens.data.local.entity.ScanHistoryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScanHistoryRepository @Inject constructor(
    private val scanHistoryDao: ScanHistoryDao
){
    fun getAllScans(): Flow<List<ScanHistoryEntity>>{
        return scanHistoryDao.getAllScans()
    }

    suspend fun deleteScan(id:Int){
        scanHistoryDao.deleteScan(id)
    }

    suspend fun clearHistory(){
        scanHistoryDao.clearHistory()
    }
}