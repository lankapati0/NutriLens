package com.akhilav.nutrilens.ui.navigation

sealed class Screen(val route:String){
    data object Home : Screen("home")
    data object Scanner : Screen("scanner")
    data object Tracker : Screen("tracker")
    data object Analytics : Screen("analytics")
    data object Profile : Screen("profile")

    data object Favorites : Screen("favorites")
    data object History : Screen("history")
    data object Compare : Screen("compare")
    data object Alternatives : Screen("alternatives")
    data object Ingredients : Screen("ingredients")
    data object MealPlanner : Screen("meal_planner")
    data object MicroNutrients: Screen("micro_nutrients")
    data object WaterTracker : Screen("water_tracker")
    data object Reports : Screen("reports")
    data object Settings : Screen("settings")
    data object WeightTracker : Screen("weight_track")
    data object FastingTracker : Screen("fasting")
    data object TrackDiet : Screen("track_diet")
}