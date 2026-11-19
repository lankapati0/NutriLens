package com.akhilav.nutrilens.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_history")
data class ScanHistoryEntity(
    @PrimaryKey
    val id:Int= 0,
    val barcode:String,
    val productName: String?,
    val scannedAt: Long = System.currentTimeMillis()
)
