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
    data object SleepTracker: Screen("sleep_tracker")
    data object SleepDetail : Screen("sleep_detail")
    data object Reports : Screen("reports")
    data object Settings : Screen("settings")
    data object WeightTracker : Screen("weight_track")
    data object FastingTracker : Screen("fasting")
    data object TrackDiet : Screen("track_diet")
    data object WeightDetail : Screen("weight_detail")
    data object WaterDetail : Screen("water_detail")
    data object FastingDetail : Screen("fasting_detail")
    data object HistoryDetail : Screen("history_detail")
    data object MealPlannerDetail : Screen("meal_planner_detail")

    /*    data object "Dashboard Overview", "dummy1"),
    data object"Nutrition Insights", "dummy2"),
    DrawerNavItem("Fitness Goals", "dummy3"),
    DrawerNavItem("Health Reports", "dummy4"),
    DrawerNavItem("Daily Challenges", "dummy5"),

    DrawerNavItem("Activity History", "dummy7"),
    DrawerNavItem("Wellness Tips", "dummy8"),
    DrawerNavItem("My Achievements", "dummy9"),
    DrawerNavItem("App Preferences", "dummy10"),*/
}