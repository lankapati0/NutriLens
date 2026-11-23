package com.akhilav.nutrilens.ui.screens.mealPlanner

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.akhilav.nutrilens.ui.theme.BrandCoral
import com.akhilav.nutrilens.ui.theme.BrandDeepTeal
import com.akhilav.nutrilens.ui.theme.BrandOceanTeal
import com.akhilav.nutrilens.ui.component.GlassCard
import com.akhilav.nutrilens.viewmodels.mealplanner.MealPlanViewModel
import java.text.DateFormat
import java.util.Date
import java.util.Locale


@Composable
fun MealPlannerScreen(
    viewModel: MealPlanViewModel = hiltViewModel()
) {
    val meals by viewModel.meals.collectAsStateWithLifecycle()
    val dateFormatter = remember {
        DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault())
    }
    var showAddMealDialog by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Meal Planner",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                showAddMealDialog = true
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = BrandDeepTeal,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add Meal")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (meals.isEmpty()) {
            GlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.RestaurantMenu,
                        contentDescription = null,
                        tint = BrandOceanTeal,
                        modifier = Modifier.size(40.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "No meals planned yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(meals, key = { it.id }) { meal ->

                    val displayDate = remember(meal.plannedDate) {
                        try {
                            val parsedDate = java.time.LocalDate.parse(meal.plannedDate)

                            dateFormatter.format(
                                java.util.Date.from(
                                    parsedDate.atStartOfDay(
                                        java.time.ZoneId.systemDefault()
                                    ).toInstant()
                                )
                            )
                        } catch (e: Exception) {
                            meal.plannedDate
                        }
                    }

                    GlassCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = meal.mealName,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "${meal.mealType} • $displayDate",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            IconButton(
                                onClick = {
                                    viewModel.deleteMeal(meal)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete meal",
                                    tint = BrandCoral
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddMealDialog) {
        var mealName by remember {
            mutableStateOf("")
        }
        var mealType by remember {
            mutableStateOf("Breakfast")
        }
        var plannedDate by remember {
            mutableStateOf(dateFormatter.format(Date()))
        }

        AlertDialog(
            onDismissRequest = {
                showAddMealDialog = false
            },
            title = {
                Text(
                    "Add Meal",
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = mealName,
                        onValueChange = { mealName = it },
                        label = { Text("Meal name") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        "Meal type",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Column {
                        listOf(
                            "Breakfast",
                            "Lunch",
                            "Snack",
                            "Dinner"
                        ).forEach { type ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = mealType == type,
                                    onClick = { mealType = type },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = BrandOceanTeal
                                    )
                                )

                                Text(
                                    text = type,
                                    modifier = Modifier.clickable {
                                        mealType = type
                                    },
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = plannedDate,
                        onValueChange = { plannedDate = it },
                        singleLine = true,
                        label = { Text("Date") },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (mealName.isNotBlank() && plannedDate.isNotBlank()) {
                            val parsedDate = dateFormatter.parse(plannedDate)
                            if (parsedDate != null) {
                                val storedDate = java.text.SimpleDateFormat(
                                    "yyyy-MM-dd",
                                    java.util.Locale.US
                                ).format(parsedDate)

                                viewModel.addMeal(
                                    mealName.trim(),
                                    mealType,
                                    storedDate
                                )
                            }
                            showAddMealDialog = false
                        }
                    }
                ) {
                    Text(
                        "Save",
                        color = BrandOceanTeal
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showAddMealDialog = false
                    }
                ) {
                    Text(
                        "Cancel",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}
