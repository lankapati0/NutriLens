package com.akhilav.nutrilens.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.akhilav.nutrilens.ui.component.AppToastHost
import com.akhilav.nutrilens.ui.screens.FoodComparison.FoodComparisonScreen
import com.akhilav.nutrilens.ui.screens.HealthyAlternatives.HealthyAlternativesScreen
import com.akhilav.nutrilens.ui.screens.Intelligence.IngredientIntelligenceScreen
import com.akhilav.nutrilens.ui.screens.fasting.FastingTrackerScreen
import com.akhilav.nutrilens.ui.screens.favorite.FavoritesScreen
import com.akhilav.nutrilens.ui.screens.history.ScanHistoryScreen
import com.akhilav.nutrilens.ui.screens.home.HomeScreen
import com.akhilav.nutrilens.ui.screens.mealPlanner.MealPlannerScreen
import com.akhilav.nutrilens.ui.screens.micronutrient.MicronutrientScreen
import com.akhilav.nutrilens.ui.screens.profile.ProfileScreen
import com.akhilav.nutrilens.ui.screens.reports.ReportsScreen
import com.akhilav.nutrilens.ui.screens.scanner.ScannerScreen
import com.akhilav.nutrilens.ui.screens.settings.SettingsScreen
import com.akhilav.nutrilens.ui.screens.sleep_track.SleepDetailScreen
import com.akhilav.nutrilens.ui.screens.sleep_track.SleepTrackScreen
import com.akhilav.nutrilens.ui.screens.stats.StatsScreen
import com.akhilav.nutrilens.ui.screens.track_diet.TrackDietScreen
import com.akhilav.nutrilens.ui.screens.tracker.TrackerScreen
import com.akhilav.nutrilens.ui.screens.waterTracker.WaterTrackerScreen
import com.akhilav.nutrilens.ui.screens.weightTrack.WeightDetailScreen
import com.akhilav.nutrilens.ui.screens.weightTrack.WeightTrackingScreen

@Composable
fun NutriLensNavGraph(
    navController: NavHostController,
    onThemeChange:(String)-> Unit,selectedTheme: String,
    modifier: Modifier = Modifier
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    AppToastHost(navigationKey = currentRoute.orEmpty()) {
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = modifier
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onScanClick = {
                        navController.navigate(Screen.Scanner.route) {
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

            composable(Screen.MicroNutrients.route) {
                MicronutrientScreen()
            }

            composable(Screen.WeightTracker.route) {
                WeightTrackingScreen(
                    onNavigateToDetail = {
                        navController.navigate("weight_detail")
                    }
                )
            }
            composable(Screen.SleepTracker.route) {
                SleepTrackScreen(
                    onSleepDetailClick = {
                        navController.navigate(Screen.SleepDetail.route)
                    }
                )
            }

            composable(Screen.SleepDetail.route) {
                SleepDetailScreen(
                    onBackClick = {
                        navController.popBackStack()

                    }
                )
            }
            composable(Screen.WeightDetail.route){
                WeightDetailScreen()
            }
            composable(Screen.TrackDiet.route) {
                TrackDietScreen()
            }

            composable(Screen.WaterTracker.route) {
                WaterTrackerScreen()
            }

            composable(Screen.FastingTracker.route) {
                FastingTrackerScreen()
            }

            composable(Screen.Reports.route) {
                ReportsScreen()
            }

            composable(Screen.Settings.route) {
                SettingsScreen(onThemeChange = onThemeChange, selectedTheme = selectedTheme)
            }

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