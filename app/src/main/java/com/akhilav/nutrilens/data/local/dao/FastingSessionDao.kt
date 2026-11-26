package com.akhilav.nutrilens.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.akhilav.nutrilens.data.local.entity.FastingSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FastingSessionDao {

    @Insert
    suspend fun insertSession(session: FastingSessionEntity): Long
    @Update
    suspend fun updateSession(session: FastingSessionEntity)

    @Query("""SELECT * FROM fasting_sessions WHERE endTime IS NULL ORDER BY startTime DESC LIMIT 1""")
    fun getActiveSession(): Flow<FastingSessionEntity?>

    @Query("""SELECT * FROM fasting_sessions ORDER BY startTime DESC""")
    fun getAllSessions() : Flow<List<FastingSessionEntity>>
}