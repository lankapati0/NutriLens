package com.akhilav.nutrilens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.akhilav.nutrilens.ui.navigation.NutriLensApp
import com.akhilav.nutrilens.ui.screens.SplashScreen
import com.akhilav.nutrilens.ui.theme.NutriLensTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NutriLensTheme {
                var showSplash by remember { mutableStateOf(true) }
                if(showSplash){
                    SplashScreen(onFinished = {
                        showSplash=false
                    })
                } else {
                    NutriLensApp()
                }
            }
        }
    }
}

