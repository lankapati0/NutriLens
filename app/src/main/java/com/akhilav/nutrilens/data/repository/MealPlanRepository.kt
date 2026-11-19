package com.akhilav.nutrilens.data.repository

import com.akhilav.nutrilens.data.local.dao.MealPlanDao
import com.akhilav.nutrilens.data.local.entity.MealPlanEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MealPlanRepository @Inject constructor(
    private val mealPlanDao: MealPlanDao
){
    fun getAllMeals(): Flow<List<MealPlanEntity>> {
        return mealPlanDao.getAllMeals()
    }

    suspend fun insertMeals(meal: MealPlanEntity){
        mealPlanDao.insertMeal(meal)
    }

    suspend fun deleteMeal(meal: MealPlanEntity){
        mealPlanDao.deleteMeal(meal = meal)
    }
}