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
import com.akhilav.nutrilens.ui.screens.HealthyAlternatives.HealthyAlternativesScreen
import com.akhilav.nutrilens.ui.screens.Intelligence.IngredientIntelligenceScreen
import com.akhilav.nutrilens.ui.screens.favorite.FavoritesScreen
import com.akhilav.nutrilens.ui.screens.history.ScanHistoryScreen
import com.akhilav.nutrilens.ui.screens.home.HomeScreen
import com.akhilav.nutrilens.ui.screens.mealPlanner.MealPlannerScreen
import com.akhilav.nutrilens.ui.screens.profile.ProfileScreen
import com.akhilav.nutrilens.ui.screens.reports.ReportsScreen
import com.akhilav.nutrilens.ui.screens.scanner.ScannerScreen
import com.akhilav.nutrilens.ui.screens.settings.SettingsScreen
import com.akhilav.nutrilens.ui.screens.stats.StatsScreen
import com.akhilav.nutrilens.ui.screens.tracker.TrackerScreen
import com.akhilav.nutrilens.ui.screens.waterTracker.WaterTrackerScreen

@Composable
fun NutriLensNavGraph(
    navController: NavHostController,
    onThemeChange:(String)-> Unit,selectedTheme: String,
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
            StatsScreen()
        }

        composable(Screen.Profile.route) {
            ProfileScreen()
        }

        composable(Screen.Favorites.route) {
            FavoritesScreen()
        }

        composable(Screen.History.route) {
            ScanHistoryScreen()
        }

        composable(Screen.Compare.route) {
            FoodComparisonScreen()
        }

        composable(Screen.Alternatives.route) {
            HealthyAlternativesScreen()
        }

        composable(Screen.Ingredients.route) {
            IngredientIntelligenceScreen()
        }

        composable(Screen.MealPlanner.route) {
            MealPlannerScreen()
        }

        composable(Screen.WaterTracker.route) {
            WaterTrackerScreen()
        }

        composable(Screen.Reports.route) {
            ReportsScreen()
        }

        composable(Screen.Settings.route) {
            SettingsScreen(onThemeChange = onThemeChange, selectedTheme = selectedTheme)
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