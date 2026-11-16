package com.akhilav.nutrilens.ui.navigation

data class DrawerNavItem(val title: String, val route:String)

val drawerItems = listOf(
    DrawerNavItem("Favorites", Screen.Favorites.route),
    DrawerNavItem("Scan History", Screen.History.route),
    DrawerNavItem("Compare Foods", Screen.Compare.route),
    DrawerNavItem("Healthy Alternatives", Screen.Alternatives.route),
    DrawerNavItem("Ingredient Intelligence", Screen.Ingredients.route),
    DrawerNavItem("Meal Planner", Screen.MealPlanner.route),
    DrawerNavItem("Water Tracker", Screen.WaterTracker.route),
    DrawerNavItem("Reports & Export", Screen.Reports.route),
    DrawerNavItem("Settings", Screen.Settings.route),
    DrawerNavItem("About NutriLens", Screen.About.route)
)