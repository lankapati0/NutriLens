package com.akhilav.nutrilens.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id:Int =1,
    val displayName: String = "",
    val profileImageUrl: String? = null,
    val heightCm: Double? = null,
    val weightKg: Double? = null,
    val dateOfBirth: String? = null,
    val measurementSystem: String = "Metric"
    )