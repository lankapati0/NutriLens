package com.akhilav.nutrilens.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.akhilav.nutriscan.ui.navigation.NutriLensScaffold

@Composable
fun NutriLensApp(){
    val navController = rememberNavController()

    NutriLensScaffold(
        navController = navController
    )
}