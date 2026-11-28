package com.akhilav.nutrilens.ui.screens.weightTrack

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingFlat
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.remote.creation.dsl.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.akhilav.nutrilens.ui.component.GlassCard
import com.akhilav.nutrilens.ui.theme.BrandCoral
import com.akhilav.nutrilens.ui.theme.BrandDeepTeal
import com.akhilav.nutrilens.ui.theme.BrandMint
import com.akhilav.nutrilens.ui.theme.BrandOceanTeal
import com.akhilav.nutrilens.viewmodels.weightEntry.WeightViewModel
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.columnModel
import com.patrykandpatrick.vico.compose.cartesian.data.lineModel
import com.patrykandpatrick.vico.compose.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.patrykandpatrick.vico.compose.common.Fill
@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun WeightTrackingScreen(onNavigateToDetail: () -> Unit,
    viewModel: WeightViewModel = hiltViewModel()
) {

    val weightHistory by viewModel.weightHistory.collectAsStateWithLifecycle()
    val latestWeight by viewModel.latestWeight.collectAsStateWithLifecycle()
    var weightInput by remember { mutableStateOf("") }

    val weightChartModelProducer = remember { CartesianChartModelProducer() }
    val weightChartValues = weightHistory.sortedBy { it.recordedAt }.map { it.weightKg }

    val weightChartLabels = weightHistory.sortedBy { it.recordedAt }.map {
        SimpleDateFormat(
            "dd MMM",
            Locale.getDefault()
        ).format(Date(it.recordedAt))
    }

    LaunchedEffect(weightChartValues) {
        if (weightChartValues.isNotEmpty()) {
            weightChartModelProducer.runTransaction {
                lineModel {
                    series(weightChartValues)
                }
            }
        }
    }


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Screen heading
        item {
            Text(
                text = "Weight Progress",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        // Weight Hero Card
        item {
            WeightHeroCard(
                currentWeight = latestWeight?.weightKg,
                previousWeight = weightHistory
                    .sortedByDescending { it.recordedAt }
                    .getOrNull(1)?.weightKg,
                onViewDetails = onNavigateToDetail
            )
        }

        // Weight Progress Chart
        if (weightChartValues.isNotEmpty()) {
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "Your Weight Journey",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Text(
                                    text = "Track your progress over time",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.ShowChart,
                                contentDescription = null,
                                tint = BrandOceanTeal,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        CartesianChartHost(
                            chart = rememberCartesianChart(
                                rememberLineCartesianLayer(
                                    lineProvider = LineCartesianLayer.LineProvider.series(
                                        LineCartesianLayer.Line(
                                            fill = LineCartesianLayer.LineFill.single(
                                                Fill(BrandOceanTeal)
                                            ),
                                            stroke = LineCartesianLayer.LineStroke.Continuous(
                                                thickness = 3.dp
                                            )
                                        )
                                    )
                                ),
                                startAxis = VerticalAxis.rememberStart(
                                    label = rememberAxisLabelComponent(
                                        style = TextStyle(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 11.sp
                                        )
                                    )
                                ),
                                bottomAxis = HorizontalAxis.rememberBottom(
                                    valueFormatter = { _, x, _ ->
                                        weightChartLabels.getOrNull(x.toInt())
                                            ?: x.toInt().toString()
                                    },
                                    itemPlacer = remember {
                                        HorizontalAxis.ItemPlacer.aligned(
                                            spacing = { 1 }
                                        )
                                    },
                                    label = rememberAxisLabelComponent(
                                        style = TextStyle(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 10.sp
                                        )
                                    ),
                                )
                            ),
                            modelProducer = weightChartModelProducer,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp).clip(RoundedCornerShape(18.dp)).background(
                                    color = BrandMint.copy(alpha=0.08f),
                                    shape = RoundedCornerShape(18.dp)
                                ).padding(8.dp)
                        )
                        if (weightChartValues.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(18.dp))

                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "Lowest",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Text(
                                        text = String.format(
                                            Locale.getDefault(),
                                            "%.1f kg",
                                            weightChartValues.minOrNull() ?: 0.0
                                        ),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = BrandOceanTeal
                                    )
                                }

                                Column(
                                    horizontalAlignment = Alignment.End
                                ) {
                                    Text(
                                        text = "Highest",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Text(
                                        text = String.format(
                                            Locale.getDefault(),
                                            "%.1f kg",
                                            weightChartValues.maxOrNull() ?: 0.0
                                        ),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = BrandCoral
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            item {
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
                            imageVector = Icons.Default.ShowChart,
                            contentDescription = null,
                            tint = BrandOceanTeal,
                            modifier = Modifier.size(40.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Your weight journey starts here",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Record your first measurement to see your progress chart.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }



        // Record Weight
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(BrandMint.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = BrandDeepTeal,
                                modifier = Modifier.size(23.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Record Weight",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "Add your latest measurement",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = weightInput,
                        onValueChange = { weightInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Weight (kg)") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandOceanTeal,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                            focusedLabelColor = BrandOceanTeal,
                            cursorColor = BrandOceanTeal
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val weight = weightInput.toDoubleOrNull()

                            if (weight != null && weight > 0) {
                                viewModel.saveWeight(weight)
                                weightInput = ""
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandDeepTeal
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Save Measurement",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Weight History Heading
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Measurement History",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Your recorded weight measurements",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    tint = BrandOceanTeal,
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "${weightHistory.size} measurements recorded",
                style = MaterialTheme.typography.labelMedium,
                color = BrandOceanTeal
            )
        }

        // Empty State
        if (weightHistory.isEmpty()) {
            item {
                Text(
                    text = "No weight history available.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            // Weight History Entries
            items(weightHistory) { entry ->
                val previousEntry = weightHistory
                    .filter { it.recordedAt < entry.recordedAt }
                    .maxByOrNull { it.recordedAt }

                val weightDifference = previousEntry?.let {
                    entry.weightKg - it.weightKg
                }
                GlassCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(BrandOceanTeal.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MonitorWeight,
                                    contentDescription = null,
                                    tint = BrandOceanTeal,
                                    modifier = Modifier.size(23.dp)
                                )
                            }

                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = SimpleDateFormat(
                                        "dd MMM yyyy",
                                        Locale.getDefault()
                                    ).format(Date(entry.recordedAt)),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Text(
                                    text = SimpleDateFormat(
                                        "hh:mm a",
                                        Locale.getDefault()
                                    ).format(Date(entry.recordedAt)),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = String.format(
                                    Locale.getDefault(),
                                    "%.1f kg",
                                    entry.weightKg
                                ),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = BrandDeepTeal
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                if (weightDifference == null) {
                                    Text(
                                        text = "First record",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                } else {
                                    Icon(
                                        imageVector = when {
                                            weightDifference < 0 -> Icons.Default.TrendingDown
                                            weightDifference > 0 -> Icons.Default.TrendingUp
                                            else -> Icons.Default.TrendingFlat
                                        },
                                        contentDescription = null,
                                        tint = when {
                                            weightDifference < 0 -> BrandOceanTeal
                                            weightDifference > 0 -> BrandCoral
                                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                                        },
                                        modifier = Modifier.size(14.dp)
                                    )

                                    Text(
                                        text = String.format(
                                            Locale.getDefault(),
                                            "%+.1f kg",
                                            weightDifference
                                        ),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = BrandOceanTeal
                                    )
                                }
                            }
                        }
                    }
                }

            }
        }
    }
}
@Composable
private fun WeightHeroCard(
    currentWeight: Double?,
    previousWeight: Double?,
    onViewDetails: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    val weightChange = if (
        currentWeight != null && previousWeight != null
    ) {
        currentWeight - previousWeight
    } else null

    Card(
        modifier = Modifier.fillMaxWidth()
            .clickable{
                onViewDetails()
            },
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = BrandDeepTeal
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "YOUR BODY WEIGHT",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.7f),
                            letterSpacing = 1.5.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Current measurement",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.MonitorWeight,
                        contentDescription = null,
                        tint = BrandMint,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.height(26.dp))

                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = currentWeight?.let {
                            String.format(Locale.getDefault(), "%.1f", it)
                        } ?: "--",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "kg",
                        style = MaterialTheme.typography.titleLarge,
                        color = BrandMint,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = when {
                            weightChange == null -> Icons.Default.Info
                            weightChange < 0 -> Icons.Default.TrendingDown
                            weightChange > 0 -> Icons.Default.TrendingUp
                            else -> Icons.Default.TrendingFlat
                        },
                        contentDescription = null,
                        tint = BrandMint,
                        modifier = Modifier.size(18.dp)
                    )

                    Text(
                        text = when {
                            weightChange == null -> "No previous measurement"
                            weightChange < 0 ->
                                "${String.format(Locale.getDefault(), "%.1f", kotlin.math.abs(weightChange))} kg down since last record"
                            weightChange > 0 ->
                                "${String.format(Locale.getDefault(), "%.1f", weightChange)} kg up since last record"
                            else -> "No change since last record"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = BrandMint
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                HorizontalDivider(
                    color = Color.White.copy(alpha = 0.18f)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Every measurement is a step towards better health.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.75f)
                )
            }
        }
    }
}