package com.akhilav.nutrilens.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.akhilav.nutrilens.data.local.dao.ProductDao
import com.akhilav.nutrilens.data.local.entity.ProductEntity

@Database(
    entities = [ProductEntity::class],
    version = 1,
    exportSchema = false
)
abstract class NutriLensDatabase : RoomDatabase(){
    abstract fun productDao(): ProductDao
}