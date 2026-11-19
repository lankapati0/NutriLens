package com.akhilav.nutrilens.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.akhilav.nutrilens.data.local.entity.MealPlanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MealPlanDao{
    @Insert
    suspend fun insertMeal(meal: MealPlanEntity)

    @Delete
    suspend fun deleteMeal(meal: MealPlanEntity)

    @Query("""SELECT * FROM meal_plans WHERE plannedDate = :date ORDER BY id ASC""")
    fun getMealsForDate(date:String): Flow<List<MealPlanEntity>>

    @Query("""SELECT * FROM meal_plans ORDER BY plannedDate ASC, id ASC """)
    fun getAllMeals():Flow<List<MealPlanEntity>>
}