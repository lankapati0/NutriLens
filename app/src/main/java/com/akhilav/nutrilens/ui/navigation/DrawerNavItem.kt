package com.akhilav.nutrilens.ui.navigation

data class DrawerNavItem(val title: String, val route:String)

val drawerItems = listOf(
    DrawerNavItem("Favorites", Screen.Favorites.route),
    DrawerNavItem("Scan History", Screen.History.route),
    DrawerNavItem("Compare Foods", Screen.Compare.route),
    DrawerNavItem("Healthy Alternatives", Screen.Alternatives.route),
    DrawerNavItem("Ingredient Intelligence", Screen.Ingredients.route),
    DrawerNavItem("Meal Planner", Screen.MealPlanner.route),
    DrawerNavItem("Track Diet", Screen.TrackDiet.route),
    DrawerNavItem("Micronutrients",Screen.MicroNutrients.route),
    DrawerNavItem("Weight Tracker", Screen.WeightTracker.route),
    DrawerNavItem("Water Tracker", Screen.WaterTracker.route),
    DrawerNavItem("Fasting Tracker", Screen.FastingTracker.route),
    DrawerNavItem("Reports & Export", Screen.Reports.route),
    DrawerNavItem("Settings", Screen.Settings.route),
/*    DrawerNavItem("Dashboard Overview", "dummy1"),
    DrawerNavItem("Nutrition Insights", "dummy2"),
    DrawerNavItem("Fitness Goals", "dummy3"),
    DrawerNavItem("Health Reports", "dummy4"),
    DrawerNavItem("Daily Challenges", "dummy5"),
    DrawerNavItem("Sleep Tracker", "dummy6"),
    DrawerNavItem("Activity History", "dummy7"),
    DrawerNavItem("Wellness Tips", "dummy8"),
    DrawerNavItem("My Achievements", "dummy9"),
    DrawerNavItem("App Preferences", "dummy10"),*/
)