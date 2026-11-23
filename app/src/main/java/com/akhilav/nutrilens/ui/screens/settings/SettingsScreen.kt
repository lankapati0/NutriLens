package com.akhilav.nutrilens.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Policy
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.akhilav.nutrilens.viewmodels.settings.SettingsViewModel
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import com.akhilav.nutrilens.ui.theme.BrandDeepTeal
import com.akhilav.nutrilens.ui.theme.BrandOceanTeal
import com.akhilav.nutrilens.ui.component.GlassCard
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun SettingsScreen(
    onThemeChange: (String) -> Unit,
    selectedTheme: String,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val calorieTarget by settingsViewModel.dailyCalorieTarget.collectAsStateWithLifecycle()
    val proteinTarget by settingsViewModel.dailyProteinTarget.collectAsStateWithLifecycle()
    val carbsTarget by settingsViewModel.dailyCarbsTarget.collectAsStateWithLifecycle()
    val fatTarget by settingsViewModel.dailyFatTarget.collectAsStateWithLifecycle()
    val waterTarget by settingsViewModel.dailyWaterTarget.collectAsStateWithLifecycle()

    val mealRemindersEnabled by settingsViewModel.mealRemindersEnabled.collectAsStateWithLifecycle()
    val mealReminderHour by settingsViewModel.mealReminderHour.collectAsStateWithLifecycle()
    val mealReminderMinute by settingsViewModel.mealReminderMinute.collectAsStateWithLifecycle()
    var showMealTimePicker by remember { mutableStateOf(false) }

    val waterRemindersEnabled by settingsViewModel.waterRemindersEnabled.collectAsStateWithLifecycle()
    val waterReminderHour by settingsViewModel.waterReminderHour.collectAsStateWithLifecycle()
    val waterReminderMinute by settingsViewModel.waterReminderMinute.collectAsStateWithLifecycle()

    val veganEnabled by settingsViewModel.veganEnabled.collectAsStateWithLifecycle()
    val vegetarianEnabled by settingsViewModel.vegetarianEnabled.collectAsStateWithLifecycle()
    val glutenFreeEnabled by settingsViewModel.glutenFreeEnabled.collectAsStateWithLifecycle()
    val dairyFreeEnabled by settingsViewModel.dairyFreeEnabled.collectAsStateWithLifecycle()

    val notificationsEnabled by settingsViewModel.notificationEnabled.collectAsStateWithLifecycle()

    var showWaterTimePicker by remember { mutableStateOf(false) }

    var editingGoal by remember { mutableStateOf<String?>(null) }
    var goalInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Theme
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Appearance",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    "Choose your preferred theme",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SegmentedButton(
                        selected = selectedTheme == "System",
                        onClick = { onThemeChange("System") },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3)
                    ) {
                        Text("Auto")
                    }

                    SegmentedButton(
                        selected = selectedTheme == "Light",
                        onClick = { onThemeChange("Light") },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.LightMode,
                            contentDescription = "Light theme"
                        )
                    }

                    SegmentedButton(
                        selected = selectedTheme == "Dark",
                        onClick = { onThemeChange("Dark") },
                        shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.DarkMode,
                            contentDescription = "Dark theme"
                        )
                    }
                }
            }
        }

        // Daily Goals
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Daily Goals",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    "Set your daily nutrition targets",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                GoalRow("Calories", "$calorieTarget kcal") {
                    editingGoal = "Calories"
                    goalInput = calorieTarget.toString()
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                GoalRow("Protein", "$proteinTarget g") {
                    editingGoal = "Protein"
                    goalInput = proteinTarget.toString()
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                GoalRow("Carbohydrates", "$carbsTarget g") {
                    editingGoal = "Carbohydrates"
                    goalInput = carbsTarget.toString()
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                GoalRow("Fat", "$fatTarget g") {
                    editingGoal = "Fat"
                    goalInput = fatTarget.toString()
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                GoalRow("Water", "$waterTarget ml") {
                    editingGoal = "Water"
                    goalInput = waterTarget.toString()
                }
            }
        }

        // Dietary Preferences
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier= Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Dietary Preferences",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    "Choose your dietary requirements",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                DietaryPreferenceRow("Vegan",veganEnabled){
                    settingsViewModel.saveVeganEnabled(it)
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                DietaryPreferenceRow("Vegetarian",vegetarianEnabled){
                    settingsViewModel.saveVegetarianEnabled(it)
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                DietaryPreferenceRow("Gluten-free",glutenFreeEnabled){
                    settingsViewModel.saveGlutenFreeEnabled(it)
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                DietaryPreferenceRow("Dairy-free",dairyFreeEnabled){
                    settingsViewModel.saveDairyFreeEnabled(it)
                }

            }
        }



        // Notifications
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Notifications",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        if (notificationsEnabled) {
                            "Reminders are enabled"
                        } else {
                            "All scheduled reminders are disabled"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = {
                        settingsViewModel.saveNotificationEnabled(it)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = BrandOceanTeal
                    )
                )
            }
        }

        // Meal Reminders
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Meal Reminders",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            if (mealRemindersEnabled) "Enabled" else "Disabled",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (mealRemindersEnabled) {
                                BrandOceanTeal
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }

                    Switch(
                        checked = mealRemindersEnabled,
                        onCheckedChange = {
                            settingsViewModel.saveMealRemindersEnabled(it)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = BrandOceanTeal
                        )
                    )
                }

                OutlinedButton(
                    onClick = { showMealTimePicker = true },
                    enabled = mealRemindersEnabled,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = BrandDeepTeal
                    )
                ) {
                    Icon(
                        Icons.Outlined.AccessTime,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        String.format(
                            Locale.getDefault(),
                            "Reminder time: %02d:%02d",
                            mealReminderHour,
                            mealReminderMinute
                        )
                    )
                }
            }
        }

        // Water Reminders
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Water Reminders",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            if (waterRemindersEnabled) "Enabled" else "Disabled",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (waterRemindersEnabled) {
                                BrandOceanTeal
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }

                    Switch(
                        checked = waterRemindersEnabled,
                        onCheckedChange = {
                            settingsViewModel.saveWaterRemindersEnabled(it)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = BrandOceanTeal
                        )
                    )
                }

                OutlinedButton(
                    onClick = { showWaterTimePicker = true },
                    enabled = waterRemindersEnabled,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = BrandDeepTeal
                    )
                ) {
                    Icon(
                        Icons.Outlined.AccessTime,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        String.format(
                            Locale.getDefault(),
                            "Reminder time: %02d:%02d",
                            waterReminderHour,
                            waterReminderMinute
                        )
                    )
                }
            }
        }

        // About
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.Info,
                        contentDescription = null,
                        tint = BrandOceanTeal
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "About NutriLens",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    "NutriLens helps you understand food nutrition, " +
                            "track daily meals, monitor water intake, " +
                            "and make informed food choices.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Text(
                    "Version 1.0",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Legal
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Legal",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.Policy,
                        contentDescription = null,
                        tint = BrandOceanTeal
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Privacy Policy",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.Description,
                        contentDescription = null,
                        tint = BrandOceanTeal
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Terms of Use",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }

    // Meal Time Picker
    if (showMealTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = mealReminderHour,
            initialMinute = mealReminderMinute,
            is24Hour = true
        )

        TimePickerDialog(
            onDismissRequest = {
                showMealTimePicker = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        settingsViewModel.saveMealRemindersTime(
                            timePickerState.hour,
                            timePickerState.minute
                        )
                        showMealTimePicker = false
                    }
                ) {
                    Text("Save", color = BrandOceanTeal)
                }
            },
            title = {
                Text("Meal Reminder Time")
            },
            dismissButton = {
                TextButton(
                    onClick = { showMealTimePicker = false }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            TimePicker(state = timePickerState)
        }
    }

    // Edit Goal Dialog
    if (editingGoal != null) {
        AlertDialog(
            onDismissRequest = {
                editingGoal = null
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        editingGoal = null
                    }
                ) {
                    Text("Cancel")
                }
            },
            title = {
                Text("Edit $editingGoal Goal")
            },
            text = {
                OutlinedTextField(
                    value = goalInput,
                    onValueChange = { goalInput = it },
                    singleLine = true,
                    label = { Text("Daily target") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = if (editingGoal == "Water") {
                            KeyboardType.Number
                        } else {
                            KeyboardType.Decimal
                        }
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        when (editingGoal) {
                            "Calories" -> settingsViewModel.saveCalorieTarget(goalInput.toDouble())
                            "Protein" -> settingsViewModel.saveProteinTarget(goalInput.toDouble())
                            "Carbohydrates" -> settingsViewModel.saveCarbsTarget(goalInput.toDouble())
                            "Fat" -> settingsViewModel.saveFatTarget(goalInput.toDouble())
                            "Water" -> settingsViewModel.saveWaterTarget(goalInput.toInt())
                        }
                        editingGoal = null
                    },
                    enabled = if (editingGoal == "Water") {
                        (goalInput.toIntOrNull() ?: 0) > 0
                    } else {
                        (goalInput.toDoubleOrNull() ?: 0.0) > 0.0
                    }
                ) {
                    Text("Save", color = BrandOceanTeal)
                }
            }
        )
    }

    // Water Time Picker
    if (showWaterTimePicker) {
        val waterTimePickerState = rememberTimePickerState(
            initialHour = waterReminderHour,
            initialMinute = waterReminderMinute,
            is24Hour = true
        )

        TimePickerDialog(
            onDismissRequest = {
                showWaterTimePicker = false
            },
            title = {
                Text("Water Reminder Time")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        settingsViewModel.saveWaterReminderTime(
                            waterTimePickerState.hour,
                            waterTimePickerState.minute
                        )
                        showWaterTimePicker = false
                    }
                ) {
                    Text("Save", color = BrandOceanTeal)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showWaterTimePicker = false }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            TimePicker(state = waterTimePickerState)
        }
    }
}

@Composable
fun GoalRow(title: String, value: String, onEdit: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        TextButton(
            onClick = onEdit,
            colors = ButtonDefaults.textButtonColors(
                contentColor = BrandOceanTeal
            )
        ) {
            Text("Edit")
        }
    }
}
@Composable
fun DietaryPreferenceRow(title:String,checked: Boolean,onCheckedChange:(Boolean)-> Unit){
    Row(modifier = Modifier.fillMaxWidth(),verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text=title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface)
        Switch(checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = BrandOceanTeal
            )
        )
    }
}