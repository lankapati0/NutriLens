package com.akhilav.nutrilens.data.repository

import com.akhilav.nutrilens.data.local.dao.FastingSessionDao
import com.akhilav.nutrilens.data.local.entity.FastingSessionEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FastingRepository @Inject constructor(
    private val fastingSessionDao: FastingSessionDao
){
    fun getActiveSession(): Flow<FastingSessionEntity?>{
        return fastingSessionDao.getActiveSession()
    }

    fun getAllSessions(): Flow<List<FastingSessionEntity>>{
        return fastingSessionDao.getAllSessions()
    }

    suspend fun startFast(targetHours:Int){
        fastingSessionDao.insertSession(
            FastingSessionEntity(startTime = System.currentTimeMillis(), targetHours=targetHours)
        )
    }

    suspend fun endFast(session: FastingSessionEntity){
        fastingSessionDao.updateSession(session.copy(endTime = System.currentTimeMillis()))
    }

}