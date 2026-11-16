package com.akhilav.nutrilens.di

import android.content.Context
import androidx.room.Room
import com.akhilav.nutrilens.data.local.dao.ProductDao
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

    fun provideDatabase(@ApplicationContext context: Context): NutriLensDatabase{
        return Room.databaseBuilder(context,
            NutriLensDatabase::class.java, "nutrilens_database").build()
    }

    @Provides
    @Singleton
    fun provideProductDao(database: NutriLensDatabase): ProductDao {
        return database.productDao()
    }
}