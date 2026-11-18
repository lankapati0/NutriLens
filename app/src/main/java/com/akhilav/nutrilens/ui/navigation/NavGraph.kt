package com.akhilav.nutrilens.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.akhilav.nutrilens.ui.screens.FoodComparison.FoodComparisonScreen
import com.akhilav.nutrilens.ui.screens.Intelligence.IngredientIntelligenceScreen
import com.akhilav.nutrilens.ui.screens.home.HomeScreen
import com.akhilav.nutrilens.ui.screens.scanner.ScannerScreen
import com.akhilav.nutrilens.ui.screens.tracker.TrackerScreen

@Composable
fun NutriLensNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onScanClick = {
                    navController.navigate(Screen.Scanner.route){
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.Scanner.route) {
            ScannerScreen()
        }

        composable(Screen.Tracker.route) {
            TrackerScreen()
        }

        composable(Screen.Analytics.route) {
            DestinationPlaceholder("Analytics")
        }

        composable(Screen.Profile.route) {
            DestinationPlaceholder("Profile")
        }

        composable(Screen.Favorites.route) {
            DestinationPlaceholder("Favorites")
        }

        composable(Screen.History.route) {
            DestinationPlaceholder("Scan History")
        }

        composable(Screen.Compare.route) {
            FoodComparisonScreen()
        }

        composable(Screen.Alternatives.route) {
            DestinationPlaceholder("Healthy Alternatives")
        }

        composable(Screen.Ingredients.route) {
            IngredientIntelligenceScreen()
        }

        composable(Screen.MealPlanner.route) {
            DestinationPlaceholder("Meal Planner")
        }

        composable(Screen.WaterTracker.route) {
            DestinationPlaceholder("Water Tracker")
        }

        composable(Screen.Reports.route) {
            DestinationPlaceholder("Reports & Export")
        }

        composable(Screen.Settings.route) {
            DestinationPlaceholder("Settings")
        }

        composable(Screen.About.route) {
            DestinationPlaceholder("About NutriLens")
        }
    }
}

@Composable
private fun DestinationPlaceholder(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium
        )
    }
}