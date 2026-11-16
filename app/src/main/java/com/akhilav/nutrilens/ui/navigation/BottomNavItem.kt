package com.akhilav.nutrilens.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PersonPin
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val title:String,
    val route:String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(
        title = "Home",
        route = Screen.Home.route,
        icon = Icons.Default.Home
    ),
    BottomNavItem(
        title = "Scan",
        route = Screen.Scanner.route,
        icon = Icons.Default.QrCodeScanner
    ),
    BottomNavItem(
        title = "Track",
        route = Screen.Tracker.route,
        icon = Icons.Default.Restaurant
    ),
    BottomNavItem(
        title = "Stats",
        route = Screen.Analytics.route,
        icon = Icons.Default.Analytics
    ),
    BottomNavItem(
        title = "Profile",
        route = Screen.Profile.route,
        icon = Icons.Default.PersonPin
    )
)