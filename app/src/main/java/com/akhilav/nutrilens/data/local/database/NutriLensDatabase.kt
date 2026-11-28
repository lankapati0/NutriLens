package com.akhilav.nutrilens.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.akhilav.nutrilens.data.local.dao.FastingSessionDao
import com.akhilav.nutrilens.data.local.dao.FoodConsumptionDao
import com.akhilav.nutrilens.data.local.dao.MealPlanDao
import com.akhilav.nutrilens.data.local.dao.ProductDao
import com.akhilav.nutrilens.data.local.dao.ScanHistoryDao
import com.akhilav.nutrilens.data.local.dao.SleepDao
import com.akhilav.nutrilens.data.local.dao.UserProfileDao
import com.akhilav.nutrilens.data.local.dao.WaterIntakeDao
import com.akhilav.nutrilens.data.local.dao.WeightEntryDao
import com.akhilav.nutrilens.data.local.entity.FastingSessionEntity
import com.akhilav.nutrilens.data.local.entity.FoodConsumptionEntity
import com.akhilav.nutrilens.data.local.entity.MealPlanEntity
import com.akhilav.nutrilens.data.local.entity.ProductEntity
import com.akhilav.nutrilens.data.local.entity.ScanHistoryEntity
import com.akhilav.nutrilens.data.local.entity.SleepEntity
import com.akhilav.nutrilens.data.local.entity.UserProfileEntity
import com.akhilav.nutrilens.data.local.entity.WaterIntakeEntity
import com.akhilav.nutrilens.data.local.entity.WeightEntryEntity

@Database(
    entities = [ProductEntity::class, FoodConsumptionEntity::class, ScanHistoryEntity::class, MealPlanEntity::class,
        WaterIntakeEntity::class, UserProfileEntity::class, WeightEntryEntity::class,
        FastingSessionEntity::class, SleepEntity::class],
    version = 17,
    exportSchema = false
)
abstract class NutriLensDatabase : RoomDatabase(){
    abstract fun productDao(): ProductDao
    abstract fun foodConsumptionDao(): FoodConsumptionDao
    abstract fun scanHistoryDao(): ScanHistoryDao
    abstract fun mealPlanDao(): MealPlanDao
    abstract fun walterIntakeDao(): WaterIntakeDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun weightEntryDao(): WeightEntryDao
    abstract fun fastingSessionDao(): FastingSessionDao
    abstract fun sleepDao(): SleepDao
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

        val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS user_profile (
                id INTEGER NOT NULL PRIMARY KEY,
                displayName TEXT NOT NULL,
                dateOfBirth TEXT,
                heightCm REAL,
                weightKg REAL,
                profileImageUrl TEXT,
                measurementSystem TEXT NOT NULL
            )
            """.trimIndent()
                )
            }
        }
        val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE user_profile ADD COLUMN bmrSex TEXT")
                db.execSQL("ALTER TABLE user_profile ADD COLUMN activityLevel TEXT")
            }
        }
        val MIGRATION_10_11 = object : Migration(10, 11) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE food_consumption ADD COLUMN mealType TEXT NOT NULL DEFAULT 'Other'"
                )
            }
        }
        val MIGRATION_11_12 = object : Migration(11, 12) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS weight_entries (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                weightKg REAL NOT NULL,
                recordedAt INTEGER NOT NULL
            )
            """.trimIndent()
                )
            }
        }
        val MIGRATION_12_13 = object : Migration(12, 13) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE food_consumption ADD COLUMN vitaminA REAL")
                db.execSQL("ALTER TABLE food_consumption ADD COLUMN vitaminC REAL")
                db.execSQL("ALTER TABLE food_consumption ADD COLUMN vitaminD REAL")
                db.execSQL("ALTER TABLE food_consumption ADD COLUMN vitaminB12 REAL")
                db.execSQL("ALTER TABLE food_consumption ADD COLUMN calcium REAL")
                db.execSQL("ALTER TABLE food_consumption ADD COLUMN iron REAL")
                db.execSQL("ALTER TABLE food_consumption ADD COLUMN potassium REAL")
                db.execSQL("ALTER TABLE food_consumption ADD COLUMN magnesium REAL")
                db.execSQL("ALTER TABLE food_consumption ADD COLUMN zinc REAL")
            }
        }
        val MIGRATION_13_14 = object : Migration(13, 14) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE products ADD COLUMN vitaminA REAL")
                db.execSQL("ALTER TABLE products ADD COLUMN vitaminC REAL")
                db.execSQL("ALTER TABLE products ADD COLUMN vitaminD REAL")
                db.execSQL("ALTER TABLE products ADD COLUMN vitaminB12 REAL")
                db.execSQL("ALTER TABLE products ADD COLUMN calcium REAL")
                db.execSQL("ALTER TABLE products ADD COLUMN iron REAL")
                db.execSQL("ALTER TABLE products ADD COLUMN potassium REAL")
                db.execSQL("ALTER TABLE products ADD COLUMN magnesium REAL")
                db.execSQL("ALTER TABLE products ADD COLUMN zinc REAL")
            }
        }

        val MIGRATION_14_15 = object : Migration(14,15) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE products ADD COLUMN fatLevel TEXT")
                db.execSQL("ALTER TABLE products ADD COLUMN saturatedFatLevel TEXT")
                db.execSQL("ALTER TABLE products ADD COLUMN sugarLevel TEXT")
                db.execSQL("ALTER TABLE products ADD COLUMN saltLevel TEXT")

                db.execSQL("ALTER TABLE products ADD COLUMN additivesCount INTEGER")
                db.execSQL("ALTER TABLE products ADD COLUMN additivesTags TEXT")
                db.execSQL("ALTER TABLE products ADD COLUMN allergens TEXT")
                db.execSQL("ALTER TABLE products ADD COLUMN allergensTags TEXT")
                db.execSQL("ALTER TABLE products ADD COLUMN tracesTags TEXT")
                db.execSQL("ALTER TABLE products ADD COLUMN ingredientsAnalysisTags TEXT")
                db.execSQL("ALTER TABLE products ADD COLUMN labelsTags TEXT")
                db.execSQL("ALTER TABLE products ADD COLUMN ecoScoreGrade TEXT")
            }
        }
        val MIGRATION_15_16 = object : Migration(15, 16) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS fasting_sessions (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                startTime INTEGER NOT NULL,
                endTime INTEGER,
                targetHours INTEGER NOT NULL
            )
            """.trimIndent()
                )
            }
        }
        val MIGRATION_16_17 = object : Migration(16, 17) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS sleep_records (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                date TEXT NOT NULL,
                bedtime TEXT NOT NULL,
                wakeUpTime TEXT NOT NULL,
                totalSleepMinutes INTEGER NOT NULL,
                awakeMinutes INTEGER NOT NULL,
                remMinutes INTEGER NOT NULL,
                lightMinutes INTEGER NOT NULL,
                deepMinutes INTEGER NOT NULL,
                sleepScore INTEGER NOT NULL,
                sleepGoalMinutes INTEGER NOT NULL
            )
            """.trimIndent()
                )
            }
        }

        


    }
}