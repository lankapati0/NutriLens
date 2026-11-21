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
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.remote.creation.dsl.fillMaxWidth
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
import com.akhilav.nutrilens.ui.theme.component.GlassCard
import com.akhilav.nutrilens.viewmodels.report.ReportsViewModel


@Composable
fun ReportsScreen(viewModel: ReportsViewModel = hiltViewModel()) {
    val context = LocalContext.current

    val dailySummary by viewModel.dailyNutritionSummary.collectAsStateWithLifecycle()
    val dailyConsumptions by viewModel.dailyConsumptions.collectAsStateWithLifecycle()
    val weeklyConsumptions by viewModel.weeklyConsumptions.collectAsStateWithLifecycle()
    val monthlyConsumptions by viewModel.monthlyConsumptions.collectAsStateWithLifecycle()
    val waterTotal by viewModel.todayWaterTotal.collectAsStateWithLifecycle()

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
                            else -> "${
                                weeklyConsumptions.sumOf { it.calories ?: 0.0 }.toInt()
                            } kcal"
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
