package com.akhilav.nutrilens.utils

import android.content.Context
import androidx.core.content.edit

class ThemePreferences(context: Context){
    private val preferences = context.getSharedPreferences(
        "theme_preferences",
        Context.MODE_PRIVATE
    )

    fun getTheme(): String {
        return preferences.getString("app_theme", "System") ?: "System"
    }

    fun saveTheme(theme: String) {
        preferences.edit {
            putString("app_theme", theme)
        }
    }
}