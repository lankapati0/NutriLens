package com.akhilav.nutrilens.data.repository

import com.akhilav.nutrilens.data.local.dao.WeightEntryDao
import com.akhilav.nutrilens.data.local.entity.WeightEntryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeightRepository @Inject constructor(
    private val weightEntryDao: WeightEntryDao
){
    suspend fun saveWeight(weightKg: Double){
        weightEntryDao.insertWeight(WeightEntryEntity(
            weightKg = weightKg
        ))
    }

    fun getWeightHistory(): Flow<List<WeightEntryEntity>>{
        return weightEntryDao.getAllWeightEntries()
    }

    fun getLatestWeight(): Flow<WeightEntryEntity?> {
        return weightEntryDao.getLatestWeight()
    }
}