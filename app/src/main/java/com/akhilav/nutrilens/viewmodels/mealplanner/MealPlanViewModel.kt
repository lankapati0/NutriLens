package com.akhilav.nutrilens.viewmodels.mealplanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akhilav.nutrilens.data.local.entity.MealPlanEntity
import com.akhilav.nutrilens.data.repository.MealPlanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MealPlanViewModel @Inject constructor(
    private val repository: MealPlanRepository
): ViewModel(){
    val meals = repository.getAllMeals().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun addMeal(mealName: String, mealType: String, plannedDate: String){
        viewModelScope.launch {
            repository.insertMeals(
                MealPlanEntity(
                    mealName = mealName,
                    mealType = mealType,
                    plannedDate = plannedDate
                )
            )
        }
    }

    fun deleteMeal(meal: MealPlanEntity){
        viewModelScope.launch {
            repository.deleteMeal(meal)
        }
    }


}