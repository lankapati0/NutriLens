package com.akhilav.nutrilens.ui.screens.reports

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.akhilav.nutrilens.ui.theme.BrandDeepTeal
import com.akhilav.nutrilens.ui.theme.BrandOceanTeal
import com.akhilav.nutrilens.ui.component.GlassCard
import com.akhilav.nutrilens.ui.component.NutritionCircularGraph
import com.akhilav.nutrilens.ui.theme.BrandCoral
import com.akhilav.nutrilens.ui.theme.BrandGold
import com.akhilav.nutrilens.ui.theme.BrandMint
import com.akhilav.nutrilens.viewmodels.report.ReportsViewModel


@Composable
fun ReportsScreen(viewModel: ReportsViewModel = hiltViewModel()) {
    val context = LocalContext.current

    val dailySummary by viewModel.dailyNutritionSummary.collectAsStateWithLifecycle()
    val dailyConsumptions by viewModel.dailyConsumptions.collectAsStateWithLifecycle()
    val weeklyConsumptions by viewModel.weeklyConsumptions.collectAsStateWithLifecycle()
    val monthlyConsumptions by viewModel.monthlyConsumptions.collectAsStateWithLifecycle()
    val waterTotal by viewModel.todayWaterTotal.collectAsStateWithLifecycle()
    val calorieTarget by viewModel.dailyCalorieTarget.collectAsStateWithLifecycle()
    val proteinTarget by viewModel.dailyProteinTarget.collectAsStateWithLifecycle()
    val carbsTarget by viewModel.dailyCarbsTarget.collectAsStateWithLifecycle()
    val fatTarget by viewModel.dailyFatTarget.collectAsStateWithLifecycle()
    val waterTarget by viewModel.dailyWaterTarget.collectAsStateWithLifecycle()

    val nutritionInsights = buildList {
        val calories = dailySummary?.calories
        val protein = dailySummary?.protein
        val carbs = dailySummary?.carbohydrates
        val fat = dailySummary?.fat

        if (calories == null) {
            add("Add foods with calorie information to see your calorie progress.")
        } else if (calorieTarget > 0) {
            when {
                calories >= calorieTarget ->
                    add("You have reached your daily calorie goal.")

                calories >= calorieTarget * 0.75 ->
                    add("You are making good progress toward your calorie goal.")

                else ->
                    add("You are below 75% of your daily calorie goal.")
            }
        }

        if (protein == null) {
            add("Protein progress is unavailable because some food entries have missing protein data.")
        } else if (proteinTarget > 0) {
            if (protein >= proteinTarget) {
                add("Your daily protein goal has been reached.")
            } else {
                add("You need ${"%.1f".format(proteinTarget - protein)} g more protein to reach your goal.")
            }
        }

        if (carbs != null && carbsTarget > 0 && carbs > carbsTarget) {
            add("Your carbohydrate intake is above your daily target.")
        }

        if (fat != null && fatTarget > 0 && fat > fatTarget) {
            add("Your fat intake is above your daily target.")
        }

        if (waterTarget > 0) {
            if (waterTotal >= waterTarget) {
                add("Great! You have reached your daily water goal.")
            } else {
                add("You need ${waterTarget - waterTotal} ml more water to reach your goal.")
            }
        }
    }

    val exportPdf by viewModel.exportedPdf.collectAsStateWithLifecycle()
    val exportCsv by viewModel.exportedCsv.collectAsStateWithLifecycle()

    LaunchedEffect(exportPdf) {
        exportPdf?.let { file ->
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(
                Intent.createChooser(shareIntent, "Share Nutrition Report")
            )
        }
    }

    LaunchedEffect(exportCsv) {
        exportCsv?.let { file ->
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/csv"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(
                Intent.createChooser(shareIntent, "Share Nutrition Report")
            )
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Nutrition Reports",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Today's Report
        Text(
            "Today's Report",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = "Daily Nutrition Goals",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    NutritionCircularGraph(
                        title = "Calories",
                        value = dailySummary?.calories,
                        target = calorieTarget,
                        unit = "kcal",
                        color = BrandOceanTeal,
                        modifier = Modifier.weight(1f)
                    )

                    NutritionCircularGraph(
                        title = "Protein",
                        value = dailySummary?.protein,
                        target = proteinTarget,
                        unit = "g",
                        color = BrandMint,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    NutritionCircularGraph(
                        title = "Carbs",
                        value = dailySummary?.carbohydrates,
                        target = carbsTarget,
                        unit = "g",
                        color = BrandGold,
                        modifier = Modifier.weight(1f)
                    )

                    NutritionCircularGraph(
                        title = "Fat",
                        value = dailySummary?.fat,
                        target = fatTarget,
                        unit = "g",
                        color = BrandCoral,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    NutritionCircularGraph(
                        title = "Water",
                        value = waterTotal.toDouble(),
                        target = waterTarget.toDouble(),
                        unit = "ml",
                        color = Color(0xFF75B9D0),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        GlassCard(
            modifier = Modifier.fillMaxWidth()
        ){
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Nutrition Insights",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                nutritionInsights.forEach { insight ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ){
                        Text(
                            text = "✦",
                            color = BrandOceanTeal
                        )
                        Text(
                            text = insight,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        GlassCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "Calories: ${dailySummary?.calories ?: "Not available"}",
                    color = BrandOceanTeal,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    "Protein: ${dailySummary?.protein ?: "Not available"} g",
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    "Carbohydrates: ${dailySummary?.carbohydrates ?: "Not available"} g",
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    "Fat: ${dailySummary?.fat ?: "Not available"} g",
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    "Water: $waterTotal ml",
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.height(8.dp))

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    "Food entries: ${dailyConsumptions.size}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Weekly Report
        Text(
            "Weekly Report",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        GlassCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "Food entries: ${weeklyConsumptions.size}",
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    "Total calories: ${
                        when {
                            weeklyConsumptions.isEmpty() -> "No entries"
                            weeklyConsumptions.any { it.calories == null } -> "Not available"
                            else -> "${weeklyConsumptions.sumOf { it.calories ?: 0.0 }.toInt()} kcal"
                        }
                    }",
                    style = MaterialTheme.typography.titleMedium,
                    color = BrandOceanTeal
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Monthly Report
        Text(
            "Monthly Report",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        GlassCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "Food entries: ${monthlyConsumptions.size}",
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    "Total calories: ${
                        when {
                            monthlyConsumptions.isEmpty() -> "No entries"
                            monthlyConsumptions.any { it.calories == null } -> "Not available"
                            else -> "${
                                monthlyConsumptions.sumOf { it.calories ?: 0.0 }.toInt()
                            } kcal"
                        }
                    }",
                    style = MaterialTheme.typography.titleMedium,
                    color = BrandOceanTeal
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Export Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    viewModel.exportPdfReport()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandDeepTeal,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Export PDF Report")
            }

            Button(
                onClick = {
                    viewModel.exportCsvReport()
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandOceanTeal,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Export CSV")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
