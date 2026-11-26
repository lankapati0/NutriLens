package com.akhilav.nutrilens.ui.screens.track_diet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.patrykandpatrick.vico.compose.common.Fill
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.akhilav.nutrilens.viewmodels.track_diet.TrackDietViewModel
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.lineModel
import com.patrykandpatrick.vico.compose.cartesian.decoration.HorizontalLine
import com.patrykandpatrick.vico.compose.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

enum class TrendDirection{UP, DOWN, SAME}

data class NutritionChartSegment( val startDate: LocalDate,
                                  val startValue: Float,
                                  val endDate: LocalDate,
                                  val endValue: Float,
                                  val direction: TrendDirection)
@Composable
fun TrackDietScreen(
    viewModel: TrackDietViewModel = hiltViewModel()
){
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val nutrition by viewModel.selectedDateNutrition.collectAsStateWithLifecycle()
    val foodEntries by viewModel.selectedDateConsumption.collectAsStateWithLifecycle()
    val calorieTarget by viewModel.dailyCaloriesTarget.collectAsStateWithLifecycle()
    val proteinTarget by viewModel.dailyProteinTarget.collectAsStateWithLifecycle()
    val carbsTarget by viewModel.dailyCarbsTarget.collectAsStateWithLifecycle()
    val fatTarget by viewModel.dailyFatTarget.collectAsStateWithLifecycle()

    val selectedMetric = viewModel.selectedMetric.collectAsStateWithLifecycle().value
    val selectedRange = viewModel.selectedRange.collectAsStateWithLifecycle().value
    val weeklyPoints by viewModel.weeklyNutritionPoints.collectAsStateWithLifecycle()
    val monthlyPoints by viewModel.monthlyNutritionPoints.collectAsStateWithLifecycle()

    val chartPoints = when(selectedRange){
        "Week" -> weeklyPoints
        "Month" -> monthlyPoints
        else -> weeklyPoints
    }
    val chartStartDate = chartPoints.firstOrNull()?.date

    val chartValues = chartPoints.mapNotNull { point ->
        val value = when (selectedMetric) {
            "Calories" -> point.calories
            "Protein" -> point.protein
            "Carbs" -> point.carbohydrates
            "Fat" -> point.fat
            else -> null
        }
        value?.let { point.date to it.toFloat() }
    }

    val hasChartData = chartValues.any { it.second > 0f }

    val chartSegment = chartValues.zipWithNext { start, end ->
        NutritionChartSegment(
            startDate = start.first,
            startValue = start.second,
            endDate = end.first,
            endValue = end.second,
            direction = when {
                end.second > start.second -> TrendDirection.UP
                end.second < start.second -> TrendDirection.DOWN
                else -> TrendDirection.SAME
            }
        )
    }.filter { segment ->
        segment.startDate.plusDays(1) == segment.endDate
    }

    val trendUpColor = Color(0xFF22C55E)
    val trendDownColor = Color(0xFFEF4444)
    val trendSameColor = Color(0xFF9CA3AF)

    val nutritionChartModalProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(chartSegment) {
        if (hasChartData && chartSegment.isNotEmpty()) {
            nutritionChartModalProducer.runTransaction {
                lineModel {
                    chartSegment.forEachIndexed {index, segment ->
                        val startX = (segment.startDate.toEpochDay()- (chartStartDate ?: segment.startDate).toEpochDay()).toInt()
                        val endX = (segment.endDate.toEpochDay() -(chartStartDate ?: segment.startDate).toEpochDay()).toInt()
                        series(
                            listOf(startX,endX),
                            listOf(segment.startValue, segment.endValue)
                        )
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(
            rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Track Diet",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(text= selectedDate.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ){
            (0..6).forEach { index ->
                val date = selectedDate.with(DayOfWeek.MONDAY).plusDays(index.toLong())

                val isSelected = date == selectedDate
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                        .clickable {
                            viewModel.selectDate(date)
                        }
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text(text = date.format(DateTimeFormatter.ofPattern("EEE")),
                        color = if(isSelected)
                            MaterialTheme.colorScheme.onPrimary
                    else
                            MaterialTheme.colorScheme.onSurfaceVariant)

                    Text(
                        text = date.dayOfMonth.toString(),
                        color = if (isSelected)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ){
                Text(
                    text = "Today's Nutrition",
                    style = MaterialTheme.typography.titleLarge
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Column {
                        Text("Consumed")
                        Text(
                            text = nutrition?.calories?.toInt()?.toString() ?: "—",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text("kcal")
                    }
                    Column{
                        Text("Daily Goal")
                        Text(text=calorieTarget.toInt().toString(),
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text("kcal")
                    }

                    Column {
                        Text("Remaining")
                        Text(
                            text = nutrition?.calories?.let {
                                (calorieTarget - it).toInt().toString()
                            } ?: "—",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text("kcal")
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape =RoundedCornerShape(20.dp),colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ){
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ){
                Text(
                    text = "Macronutrients",
                    style = MaterialTheme.typography.titleLarge
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    MacroValue(
                        title = "Protein",
                        consumed = nutrition?.protein,
                        target = proteinTarget,
                        unit = "g"
                    )
                    MacroValue(
                        title = "Carbs",
                        consumed = nutrition?.carbohydrates,
                        target = carbsTarget,
                        unit = "g"
                    )
                    MacroValue(
                        title = "Fat",
                        consumed = nutrition?.fat,
                        target = fatTarget,
                        unit = "g"
                    )
                }
            }
        }

        Text(
            text = "Food Log",
            style = MaterialTheme.typography.titleLarge
        )
        val mealGroups = listOf(
            "Breakfast",
            "Lunch",
            "Dinner",
            "Snacks"
        )

        mealGroups.forEach { meal ->
            val mealEntries = foodEntries.filter {
                it.mealType.equals(meal, ignoreCase = true) ||
                        (meal == "Snacks" && it.mealType.equals("Other", ignoreCase = true))
            }
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = meal,
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (mealEntries.isEmpty()) {
                        Text(
                            text = "No food added",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        mealEntries.forEach { entry ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(entry.productName)
                                    Text(
                                        text = "${entry.quantityGrams.toInt()} g",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Text(
                                    text = entry.calories?.let {
                                        "${it.toInt()} kcal"
                                    } ?: "—"
                                )
                            }
                        }
                    }
                }
            }
        }

        Text(
            text = "Nutrition Trends",
            style = MaterialTheme.typography.titleLarge
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Calories", "Protein", "Carbs", "Fat").forEach { metric ->
                FilterChip(
                    selectedMetric == metric,
                    { viewModel.selectMetric(metric) },
                    label = { Text(metric) })
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ){
            listOf("Week","Month","Year","All Time").forEach { range ->
                FilterChip(
                    selectedRange == range,
                    {viewModel.selectRange(range)},
                    label = {Text(range)}
                )
            }
        }

        if(hasChartData && chartSegment.isNotEmpty()){
            CartesianChartHost(modifier = Modifier.fillMaxWidth().height(240.dp),
                modelProducer = nutritionChartModalProducer,
                chart = rememberCartesianChart(
                    rememberLineCartesianLayer(
                        lineProvider = LineCartesianLayer.LineProvider.series(
                            chartSegment.map { segment ->
                                val segmentColor = when(segment.direction){
                                    TrendDirection.UP -> trendUpColor
                                    TrendDirection.DOWN -> trendDownColor
                                    TrendDirection.SAME -> trendSameColor
                                }

                                LineCartesianLayer.rememberLine(
                                    fill = LineCartesianLayer.LineFill.single(
                                        Fill(segmentColor)
                                    ),
                                    pointProvider = LineCartesianLayer.PointProvider.single(
                                        LineCartesianLayer.Point(
                                            component = rememberShapeComponent(
                                                fill = Fill(segmentColor),
                                                shape = CircleShape
                                            ),
                                            size = 8.dp
                                        )
                                    )
                                )


                            }
                        )
                    )
                )
            )
        } else {
            Text(
                text = "Not enough nutrition data to show a trend yet.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }


    }
}

@Composable
fun MacroValue(title:String, consumed: Double?,target:Double, unit:String){
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall
        )

        Text(
            text = consumed?.let { "${it.toInt()} $unit" } ?: "—",
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = "Goal: ${target.toInt()} $unit",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}