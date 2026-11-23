package com.akhilav.nutrilens.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.akhilav.nutrilens.data.local.entity.WeightEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeightEntryDao {

    @Insert
    suspend fun insertWeight(entry: WeightEntryEntity)

    @Query("""SELECT * FROM weight_entries ORDER BY recordedAt DESC    """)
    fun getAllWeightEntries(): Flow<List<WeightEntryEntity>>

    @Query("""SELECT * FROM weight_entries ORDER BY recordedAt DESC LIMIT 1 """)
    fun getLatestWeight(): Flow<WeightEntryEntity?>
}
