package com.akhilav.nutrilens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.akhilav.nutrilens.ui.navigation.NutriLensApp
import com.akhilav.nutrilens.ui.screens.SplashScreen
import com.akhilav.nutrilens.ui.theme.NutriLensTheme
import com.akhilav.nutrilens.utils.ThemePreferences
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){
                isGranted ->

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseMessaging.getInstance().token.addOnSuccessListener { task ->
                Log.d("FCM_TOKEN", task)
            val token = task.toString()
        }.addOnFailureListener { exception ->
            Log.d("FCM_TOKEN", exception.toString())
        }
        val themePreference = ThemePreferences(this)
        setContent {
            var selectedTheme by remember { mutableStateOf(themePreference.getTheme()) }
            NutriLensTheme(
                darkTheme = when(selectedTheme){
                    "Light" -> false
                    "Dark" -> true
                    else -> isSystemInDarkTheme()
                }, dynamicColor = false
            ){
                var showSplash by remember { mutableStateOf(true) }
                if(showSplash){
                    SplashScreen(onFinished = {
                        showSplash=false
                    })
                } else {
                    NutriLensApp(
                        selectedTheme = selectedTheme,
                        onThemeChange = {theme ->
                            selectedTheme = theme
                            themePreference.saveTheme(theme)
                        }
                    )
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestNotificationPermission.launch(
                Manifest.permission.POST_NOTIFICATIONS
            )
        }
    }



}

