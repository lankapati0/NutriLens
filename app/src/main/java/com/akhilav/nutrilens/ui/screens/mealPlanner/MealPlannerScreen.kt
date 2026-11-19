package com.akhilav.nutrilens.ui.screens.mealPlanner

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
import com.akhilav.nutrilens.viewmodels.mealplanner.MealPlanViewModel
import java.text.DateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MealPlannerScreen(
    viewModel: MealPlanViewModel = hiltViewModel()
){

    val meals by viewModel.meals.collectAsStateWithLifecycle()
    var showAddMealDialog by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Meal Planner",
            style = MaterialTheme.typography.headlineMedium
        )
        Button(
            onClick = {
                showAddMealDialog = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Meal")
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (meals.isEmpty()) {
            Text("No meals planned yet.")
        } else {
            LazyColumn {
                items(meals, key = { it.id }) { meal ->
                    Card(modifier = Modifier.fillMaxWidth().padding(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(meal.mealName)
                                Text("${meal.mealType} • ${meal.plannedDate}")
                            }
                            IconButton(onClick = { viewModel.deleteMeal(meal) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete meal"
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if(showAddMealDialog){
        var mealName by remember { mutableStateOf("") }
        var mealType by remember { mutableStateOf("Breakfast") }
        val dateFormatter = remember {DateFormat.getDateInstance(
            DateFormat.SHORT, Locale.getDefault()
        )}
        var plannedDate by remember {
            mutableStateOf(dateFormatter.format(Date()))
        }
        AlertDialog(
            onDismissRequest = {
                showAddMealDialog = false
            },
            title = {
                Text("Add Meal")
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = mealName,
                        onValueChange = { mealName = it },
                        label = { Text("Meal name") },
                        singleLine = true
                    )

                    Spacer(Modifier.height(12.dp))

                    Text("Meal type")
                    Column {
                        listOf("Breakfast", "Lunch", "Snack", "Dinner").forEach { type ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = mealType == type,
                                    onClick = { mealType = type }
                                )
                                Text(
                                    text = type,
                                    modifier = Modifier.clickable { mealType = type }
                                )
                            }
                        }
                    }
                    OutlinedTextField(
                        value = plannedDate,
                        onValueChange = { plannedDate = it },
                        singleLine = true,
                        label = { Text("Date") }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (mealName.isNotBlank() && dateFormatter.toString().isNotBlank()) {
                            viewModel.addMeal(
                                mealName.trim(),
                                mealType,
                                dateFormatter.toString().trim()
                            )
                            showAddMealDialog = false
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showAddMealDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )

    }


}