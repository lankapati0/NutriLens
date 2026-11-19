package com.akhilav.nutrilens.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.akhilav.nutrilens.data.local.dao.FoodConsumptionDao
import com.akhilav.nutrilens.data.local.dao.MealPlanDao
import com.akhilav.nutrilens.data.local.dao.ProductDao
import com.akhilav.nutrilens.data.local.dao.ScanHistoryDao
import com.akhilav.nutrilens.data.local.dao.WaterIntakeDao
import com.akhilav.nutrilens.data.local.entity.FoodConsumptionEntity
import com.akhilav.nutrilens.data.local.entity.MealPlanEntity
import com.akhilav.nutrilens.data.local.entity.ProductEntity
import com.akhilav.nutrilens.data.local.entity.ScanHistoryEntity
import com.akhilav.nutrilens.data.local.entity.WaterIntakeEntity

@Database(
    entities = [ProductEntity::class, FoodConsumptionEntity::class, ScanHistoryEntity::class, MealPlanEntity::class,
        WaterIntakeEntity::class],
    version = 8,
    exportSchema = false
)
abstract class NutriLensDatabase : RoomDatabase(){
    abstract fun productDao(): ProductDao
    abstract fun foodConsumptionDao(): FoodConsumptionDao
    abstract fun scanHistoryDao(): ScanHistoryDao
    abstract fun mealPlanDao(): MealPlanDao
    abstract fun walterIntakeDao(): WaterIntakeDao
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

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE products ADD COLUMN categories TEXT"
                )
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS scan_history (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                barcode TEXT NOT NULL,
                productName TEXT,
                scannedAt INTEGER NOT NULL
            )
            """.trimIndent()
                )
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE products ADD COLUMN isFavorite INTEGER NOT NULL DEFAULT 0"
                )
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
            CREATE TABLE IF NOT EXISTS meal_plans (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                mealName TEXT NOT NULL,
                mealType TEXT NOT NULL,
                plannedDate TEXT NOT NULL
            )
        """.trimIndent())
            }
        }

        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
            CREATE TABLE meal_plans_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                mealName TEXT NOT NULL,
                mealType TEXT NOT NULL,
                plannedDate TEXT NOT NULL
            )
            """.trimIndent()
                )

                db.execSQL(
                    """
            INSERT INTO meal_plans_new (id, mealName, mealType, plannedDate)
            SELECT id, mealName, mealType, plannedDate
            FROM meal_plans
            """.trimIndent()
                )

                db.execSQL("DROP TABLE meal_plans")
                db.execSQL("ALTER TABLE meal_plans_new RENAME TO meal_plans")
            }
        }
        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS water_intake (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                amountMl INTEGER NOT NULL,
                consumedAt INTEGER NOT NULL
            )
            """.trimIndent()
                )
            }
        }

    }
}