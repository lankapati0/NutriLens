package com.akhilav.nutrilens.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.akhilav.nutrilens.data.local.dao.FoodConsumptionDao
import com.akhilav.nutrilens.data.local.dao.ProductDao
import com.akhilav.nutrilens.data.local.entity.FoodConsumptionEntity
import com.akhilav.nutrilens.data.local.entity.ProductEntity

@Database(
    entities = [ProductEntity::class, FoodConsumptionEntity::class],
    version = 2,
    exportSchema = false
)
abstract class NutriLensDatabase : RoomDatabase(){
    abstract fun productDao(): ProductDao
    abstract fun foodConsumptionDao(): FoodConsumptionDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS food_consumption (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        barcode TEXT,
                        productName TEXT NOT NULL,
                        quantityGrams REAL NOT NULL,
                        calories REAL,
                        protein REAL,
                        carbohydrates REAL,
                        fat REAL,
                        consumedAt INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }
    }
}