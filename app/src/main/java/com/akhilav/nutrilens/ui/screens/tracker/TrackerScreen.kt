package com.akhilav.nutrilens.ui.screens.tracker

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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.akhilav.nutrilens.viewmodels.HomeNutritionViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


private fun formatConsumptionTime(timestamp:Long): String {
    return SimpleDateFormat("hh:mm a",Locale.getDefault()).format(Date(timestamp))
}
@Composable
fun TrackerScreen(
    viewModel: HomeNutritionViewModel = hiltViewModel()
){
    val nutritionSummary by viewModel.nutritionSummary.collectAsStateWithLifecycle()
    val dailyCalorieTarget by viewModel.dailyCaloriesTarget.collectAsStateWithLifecycle()
    val todayConsumptions by viewModel.todaysConsumption.collectAsStateWithLifecycle()
    val dailyProteinTarget by viewModel.dailyProteinTarget.collectAsStateWithLifecycle()
    val dailyCarbsTarget by viewModel.dailyCarbsTarget.collectAsStateWithLifecycle()
    val dailyFatTarget by viewModel.dailyFatTarget.collectAsStateWithLifecycle()

    var editingTarget by remember { mutableStateOf<String?>(null) }
    var targetInput by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)
        .verticalScroll(rememberScrollState())) {
        Text(
            text = "Today's Nutrition",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(20.dp))
        //Daily Calories
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "Calories",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text=nutritionSummary?.calories?.let {
                        "${it.toInt()} kcal"
                    } ?: "-- kcal",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        "Daily target: ${dailyCalorieTarget.toInt()} kcal",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    IconButton(
                        onClick = {
                            targetInput = dailyCalorieTarget.toInt().toString()
                            editingTarget = "Calories"
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit daily calorie target",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }


                Spacer(Modifier.height(14.dp))
                val calories = nutritionSummary?.calories
                val progress = if (calories != null && dailyCalorieTarget > 0) {
                    (calories / dailyCalorieTarget).toFloat().coerceIn(0f, 1f)
                } else {
                    0f
                }
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(9.dp),
                    color = Color(0xFF087F5B),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    if (calories != null && dailyCalorieTarget > 0) {
                        "${(calories / dailyCalorieTarget * 100).toInt()}% of daily target"
                    } else {
                        "Progress will appear after tracking meals"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }


        Spacer(Modifier.height(20.dp))
        Text(
            "Macronutrients",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(12.dp))
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                MacroProgressRow(title = "Protein", consumed = nutritionSummary?.protein, target = dailyProteinTarget, color = Color(0xFF2563EB), onEdit = {
                    targetInput = dailyProteinTarget.toInt().toString()
                    editingTarget = "Protein"
                })

                MacroProgressRow(title = "Carbohydrates", consumed = nutritionSummary?.carbohydrates, target = dailyCarbsTarget, color = Color(0xFFD97706),
                    onEdit = {
                        targetInput = dailyCarbsTarget.toInt().toString()
                        editingTarget = "Carbohydrates"
                    })

                MacroProgressRow(title = "Fat", consumed = nutritionSummary?.fat, target = dailyFatTarget, color = Color(0xFFDC4444), onEdit = {
                    targetInput = dailyFatTarget.toInt().toString()
                    editingTarget = "Fat"
                })
            }
        }

        Spacer(Modifier.height(20.dp))

        Text(
            "Today's Meals",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(12.dp))
        if(todayConsumptions.isEmpty()){
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "🍽️",
                        style = MaterialTheme.typography.headlineLarge
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        "No meals logged yet",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        "Your logged foods will appear here.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                todayConsumptions.forEach { food ->
                    Card(                modifier = Modifier.fillMaxWidth()){
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ){
                            Text(text = food.productName,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Consumed: ${food.quantityGrams.toString().removeSuffix(".0")} g",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = food.calories?.let {
                                    "Calories: ${it.toInt()} kcal"
                                } ?: "Calories: Not available",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Logged at: ${formatConsumptionTime(food.consumedAt)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

    }


    if (editingTarget != null) {
        AlertDialog(
            onDismissRequest = {
                editingTarget = null
            },
            title = {
                Text("Daily $editingTarget Target")
            },
            text = {
                OutlinedTextField(
                    value = targetInput,
                    onValueChange = { input ->
                        targetInput = input
                    },
                    label = {
                        Text(
                            if (editingTarget == "Calories") {
                                "Target in kcal"
                            } else {
                                "Target in grams"
                            }
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        editingTarget = null
                    }
                ) {
                    Text("Cancel")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val target = targetInput.toDoubleOrNull()

                        if (target != null && target > 0) {
                            when (editingTarget) {
                                "Calories" ->
                                    viewModel.saveDailyCalorieTarget(target)

                                "Protein" ->
                                    viewModel.saveDailyProteinTarget(target)

                                "Carbohydrates" ->
                                    viewModel.saveDailyCarbsTarget(target)

                                "Fat" ->
                                    viewModel.saveDailyFatTarget(target)
                            }

                            editingTarget = null
                        }
                    }
                ) {
                    Text("Save")
                }
            }
        )
    }



}

@Composable
private fun MacroProgressRow(title: String,consumed: Double?,target:Double, color:Color,onEdit:()->Unit) {

    val progress = if (consumed != null && target > 0) {
        (consumed / target).toFloat().coerceIn(0f, 1f)
    } else {
        0f
    }
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title)

            Text(
                consumed?.let { "${it.toInt()} / ${target.toInt()} g" }
                    ?: "-- / ${target.toInt()} g",
                color = MaterialTheme.colorScheme.primary
            )
            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit $title target"
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}