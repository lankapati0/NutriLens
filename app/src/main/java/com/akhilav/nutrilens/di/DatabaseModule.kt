package com.akhilav.nutrilens.di

import android.content.Context
import androidx.room.Room
import androidx.work.impl.Migration_15_16
import com.akhilav.nutrilens.data.local.dao.FastingSessionDao
import com.akhilav.nutrilens.data.local.dao.FoodConsumptionDao
import com.akhilav.nutrilens.data.local.dao.MealPlanDao
import com.akhilav.nutrilens.data.local.dao.ProductDao
import com.akhilav.nutrilens.data.local.dao.ScanHistoryDao
import com.akhilav.nutrilens.data.local.dao.SleepDao
import com.akhilav.nutrilens.data.local.dao.UserProfileDao
import com.akhilav.nutrilens.data.local.dao.WaterIntakeDao
import com.akhilav.nutrilens.data.local.dao.WeightEntryDao
import com.akhilav.nutrilens.data.local.database.NutriLensDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): NutriLensDatabase{
        return Room.databaseBuilder(context,
            NutriLensDatabase::class.java, "nutrilens_database")
            .addMigrations(NutriLensDatabase.MIGRATION_1_2, NutriLensDatabase.MIGRATION_2_3,
                NutriLensDatabase.MIGRATION_3_4, NutriLensDatabase.MIGRATION_4_5,
                NutriLensDatabase.MIGRATION_5_6, NutriLensDatabase.MIGRATION_6_7,
                NutriLensDatabase.MIGRATION_7_8, NutriLensDatabase.MIGRATION_8_9,
                NutriLensDatabase.MIGRATION_9_10,NutriLensDatabase.MIGRATION_10_11,
                NutriLensDatabase.MIGRATION_11_12, NutriLensDatabase.MIGRATION_12_13,
                NutriLensDatabase.MIGRATION_13_14, NutriLensDatabase.MIGRATION_14_15,
                NutriLensDatabase.MIGRATION_15_16, NutriLensDatabase.MIGRATION_16_17)
            .build()
    }

    @Provides
    @Singleton
    fun provideProductDao(database: NutriLensDatabase): ProductDao {
        return database.productDao()
    }

    @Provides
    @Singleton
    fun provideScanHistoryDao(database: NutriLensDatabase): ScanHistoryDao {
        return database.scanHistoryDao()
    }

    @Provides
    @Singleton
    fun provideFoodConsumptionDao(database: NutriLensDatabase): FoodConsumptionDao = database.foodConsumptionDao()

    @Provides
    @Singleton
    fun provideMealPlanDao(database: NutriLensDatabase): MealPlanDao = database.mealPlanDao()

    @Provides
    @Singleton
    fun provideWaterIntakeDao(database: NutriLensDatabase): WaterIntakeDao = database.walterIntakeDao()

    @Provides
    @Singleton
    fun provideUserProfileDao(database: NutriLensDatabase): UserProfileDao = database.userProfileDao()

    @Provides
    @Singleton
    fun provideWeightEntryDao(database: NutriLensDatabase): WeightEntryDao = database.weightEntryDao()

    @Provides
    @Singleton
    fun provideFastingSessionDao(database: NutriLensDatabase): FastingSessionDao = database.fastingSessionDao()

    @Provides
    @Singleton
    fun provideSleepDao(database: NutriLensDatabase): SleepDao = database.sleepDao()
}