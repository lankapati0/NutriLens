package com.akhilav.nutrilens.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fasting_sessions")
data class FastingSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id:Long = 0,
    val startTime:Long,
    val endTime:Long? = null,
    val targetHours:Int
)