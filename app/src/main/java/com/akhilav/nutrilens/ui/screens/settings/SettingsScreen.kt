package com.akhilav.nutrilens.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.remote.creation.dsl.verticalScroll
import androidx.compose.ui.text.input.KeyboardType
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onThemeChange:(String)-> Unit,
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Text(
                text = "Theme",
                style = MaterialTheme.typography.titleMedium
            )
            SingleChoiceSegmentedButtonRow {
                SegmentedButton(
                    selected = selectedTheme == "System",
                    onClick = {onThemeChange("System")},
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3)
                ) { Text("Auto")}
                SegmentedButton(
                    selected = selectedTheme == "Light",
                    onClick = {onThemeChange("Light")},
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3)
                ) {
                    Icon(imageVector = Icons.Outlined.LightMode,contentDescription = "Light theme")
                }
                SegmentedButton(
                    selected = selectedTheme == "Dark",
                    onClick = {onThemeChange("Dark")},
                    shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3)
                ) {
                    Icon(imageVector = Icons.Outlined.DarkMode,contentDescription = "Dark theme")
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ){
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ){
                Text(
                    text="Daily Goals",
                    style = MaterialTheme.typography.titleLarge
                )

                GoalRow("Calories","${calorieTarget} kcal") {
                    editingGoal = "Calories"
                    goalInput = calorieTarget.toString()
                }

                GoalRow("Protein","${proteinTarget} g") {
                    editingGoal = "Protein"
                    goalInput = proteinTarget.toString()
                }

                GoalRow("Carbohydrates","${carbsTarget} g") {
                    editingGoal = "Carbohydrates"
                    goalInput = carbsTarget.toString()
                }

                GoalRow("Fat","${fatTarget} g") {
                    editingGoal = "Fat"
                    goalInput = fatTarget.toString()
                }

                GoalRow("Water","${waterTarget} ml") {
                    editingGoal = "Water"
                    goalInput = waterTarget.toString()
                }

            }

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
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
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            if (notificationsEnabled) {
                                "Reminders are enabled"
                            } else {
                                "All scheduled reminders are disabled"
                            },
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = {
                            settingsViewModel.saveNotificationEnabled(it)
                        })
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            Card(modifier = Modifier.fillMaxWidth()){
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Meal Reminders",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ){
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Daily meal reminder")
                            Text(
                                if (mealRemindersEnabled) "Enabled" else "Disabled",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Switch(
                            checked = mealRemindersEnabled,
                            onCheckedChange = {
                                settingsViewModel.saveMealRemindersEnabled(it)
                            }
                        )
                    }
                    OutlinedButton(
                        onClick = { showMealTimePicker = true },
                        enabled = mealRemindersEnabled,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = String.format(
                                java.util.Locale.getDefault(),
                                "Reminder time: %02d:%02d",
                                mealReminderHour,
                                mealReminderMinute
                            )
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Card(modifier = Modifier.fillMaxWidth()){
                Column(modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "Water Reminders",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ){
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Daily water reminder")
                            Text(
                                if (waterRemindersEnabled) "Enabled" else "Disabled",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Switch(
                            checked = waterRemindersEnabled,
                            onCheckedChange = {
                                settingsViewModel.saveWaterRemindersEnabled(it)
                            }
                        )
                    }
                    OutlinedButton(
                        onClick = { showWaterTimePicker = true },
                        enabled = waterRemindersEnabled,
                        modifier = Modifier.fillMaxWidth()
                    ){
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

            Spacer(modifier = Modifier.height(16.dp))
            Card(modifier= Modifier.fillMaxWidth()){
                Column(modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "About NutriLens",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        "NutriLens helps you understand food nutrition, " +
                                "track daily meals, monitor water intake, " +
                                "and make informed food choices.",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    HorizontalDivider()
                    Text(
                        "Version 1.0",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier=Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Legal",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Text(
                        "Privacy Policy",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    HorizontalDivider()

                    Text(
                        "Terms of Use",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

        }


    }

    if(showMealTimePicker){
        val timePickerState = rememberTimePickerState(initialHour = mealReminderHour, initialMinute = mealReminderMinute, is24Hour = true)

        TimePickerDialog(
            onDismissRequest = {
                showMealTimePicker = false
            },
            confirmButton = {
                TextButton(onClick = {
                    settingsViewModel.saveMealRemindersTime(timePickerState.hour, timePickerState.minute)
                    showMealTimePicker = false
                }) {Text("Save") }
            },
            title = {

            },
            dismissButton = {
                TextButton(onClick = {showMealTimePicker=false}) {Text("Cancel") }
            }
        ) {
            TimePicker(state = timePickerState)
        }

    }

    if(editingGoal != null){
        AlertDialog(
            onDismissRequest = {
                editingGoal = null
            },
            dismissButton = {
                TextButton(onClick = {
                    editingGoal = null
                }) { Text("Cancel")}
            },
            title = {
                Text("Edit ${editingGoal} Goal")
            },
            text={
                OutlinedTextField(
                    value = goalInput,
                    onValueChange = {goalInput = it},
                    singleLine = true,
                    label = {Text("Daily target")},
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
                TextButton(onClick = {
                    when(editingGoal){
                        "Calories" -> settingsViewModel.saveCalorieTarget(goalInput.toDouble())
                        "Protein" -> settingsViewModel.saveProteinTarget(goalInput.toDouble())
                        "Carbohydrates" -> settingsViewModel.saveCarbsTarget(goalInput.toDouble())
                        "Fat" -> settingsViewModel.saveFatTarget(goalInput.toDouble())
                        "Water" -> settingsViewModel.saveWaterTarget(goalInput.toInt())
                    }
                    editingGoal = null
                },
                    enabled = if(editingGoal == "Water"){
                        (goalInput.toIntOrNull() ?: 0)>0
                    }else {
                        (goalInput.toDoubleOrNull()?: 0.0)>0.0
                    }

                ) {Text("Save") }
            }
        )
    }

    if(showWaterTimePicker){
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

            },
            confirmButton = {
                TextButton(onClick = {
                    settingsViewModel.saveWaterReminderTime(
                        waterTimePickerState.hour,waterTimePickerState.minute
                    )
                    showWaterTimePicker = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = {showWaterTimePicker = false}) { Text("Cancel") }
            }
        ) {         TimePicker(state = waterTimePickerState) }
    }
}


@Composable
fun GoalRow(title:String, value:String, onEdit:()-> Unit){
    Row(modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically){
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(value, style = MaterialTheme.typography.bodyMedium)
        }
        TextButton(onClick = onEdit) {
            Text("Edit")
        }
    }
}