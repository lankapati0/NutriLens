package com.akhilav.nutrilens.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sleep_records")
data class SleepEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val date: String,
    val bedtime: String,
    val wakeUpTime: String,

    val totalSleepMinutes: Int,
    val awakeMinutes: Int,
    val remMinutes: Int,
    val lightMinutes: Int,
    val deepMinutes: Int,

    val sleepScore: Int,
    val sleepGoalMinutes: Int
)
