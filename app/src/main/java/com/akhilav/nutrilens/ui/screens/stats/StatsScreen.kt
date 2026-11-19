package com.akhilav.nutrilens.ui.screens.stats

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.akhilav.nutrilens.viewmodels.HomeNutritionViewModel
import com.patrykandpatrick.vico.compose.cartesian.CartesianChart
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.columnModel
import com.patrykandpatrick.vico.compose.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.Fill
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun StatsScreen(
    viewModel: HomeNutritionViewModel = hiltViewModel()
){

    val summary by viewModel.todaysConsumption.collectAsStateWithLifecycle()
    val calorieTarget by viewModel.dailyCaloriesTarget.collectAsStateWithLifecycle()
    val proteinTarget by viewModel.dailyProteinTarget.collectAsStateWithLifecycle()
    val carbsTarget by viewModel.dailyCarbsTarget.collectAsStateWithLifecycle()
    val fatTarget by viewModel.dailyFatTarget.collectAsStateWithLifecycle()

    val weeklyConsumption by viewModel.weeklyConsumption.collectAsStateWithLifecycle()
    val monthlyConsumption by viewModel.montlyConsumption.collectAsStateWithLifecycle()

    val totalCalories = if(summary.isEmpty()) 0.0
    else if(summary.any{it.calories == null}) null
    else summary.sumOf { it.calories!! }

    val totalProtein = if(summary.isEmpty()) 0.0
        else if(summary.any { it.protein == null }) null
        else summary.sumOf { it.protein!! }

    val totalCarbs = if(summary.isEmpty()) 0.0
        else if(summary.any { it.carbohydrates ==null }) null
        else summary.sumOf { it.carbohydrates!!}


    val totalFat = if(summary.isEmpty()) 0.0
        else if(summary.any { it.fat == null }) null
        else summary.sumOf { it.fat!!}

    val zone = ZoneId.systemDefault()
    val today = LocalDate.now(zone)

    val weeklyCalories = (6 downTo  0).map{ dayAgo ->
        val date = today.minusDays(dayAgo.toLong())
        val dayRecords = weeklyConsumption.filter {
            Instant.ofEpochMilli(it.consumedAt).atZone(zone).toLocalDate() == date
        }
        val calories = when {
            dayRecords.isEmpty() -> 0.0
            dayRecords.any { it.calories == null } -> null
            else -> dayRecords.sumOf { it.calories!! }
        }
        date to calories
    }

    val monthlyCalories = (20 downTo 0).map { daysAgo ->
        val date = today.minusDays(daysAgo.toLong())

        val dayRecords = monthlyConsumption.filter {
            Instant.ofEpochMilli(it.consumedAt).atZone(zone).toLocalDate() == date
        }
        val calories = when {
            dayRecords.isEmpty() -> 0.0
            dayRecords.any {it.calories == null} -> null
            else -> dayRecords.sumOf { it.calories!! }
        }
        date to calories
    }

    val validMonthlyCalories = monthlyCalories.mapNotNull { it.second }
    val monthlyAverage = if(monthlyCalories.isNotEmpty()){
        validMonthlyCalories.average()
    } else {null}
    val monthlyTotal = if(monthlyCalories.any { it.second == null }){
        null
    } else {
        monthlyCalories.sumOf { it.second ?: 0.0 }
    }

    val validWeeklyCalories = weeklyCalories.mapNotNull { it.second }
    val weeklyAverage = if(weeklyCalories.isNotEmpty()){
        validWeeklyCalories.average()
    }else{
        null
    }
    val weeklyTotal = if(weeklyCalories.any { it.second == null }){
        null
    } else {
        weeklyCalories.sumOf { it.second ?: 0.0 }
    }

    val calorieGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF64B5F6),
            Color(0xFF1565C0)
        )
    )

    val macroGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF81C784),
            Color(0xFF2E7D32)
        )
    )

    val chartModelProducer = remember { CartesianChartModelProducer() }
    val monthlyChartModelProducer = remember { CartesianChartModelProducer() }
    val macroChartModelProducer = remember { CartesianChartModelProducer() }

    val chartEntries = weeklyCalories.mapNotNull { (date,calories) ->
        date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()) to calories
    }

    val monthlyChartEntries = monthlyCalories.mapNotNull { (date,calories) ->
        date.dayOfMonth.toString() to calories
    }
    val monthlyChartLabels = monthlyChartEntries.mapNotNull { (label,calories) ->
        label.takeIf { calories != null }
    }
    val monthlyChartValues = monthlyChartEntries.mapNotNull { it.second }

    val chartValues = chartEntries.mapNotNull { it.second }

    val macroChartValues = if(totalProtein != null && totalCarbs != null && totalFat != null){
        listOf(totalProtein *4,totalCarbs*4,totalFat*9)
    } else {
        emptyList()
    }

    val chartLabels = chartEntries.mapNotNull { (label,calories) ->
        label.takeIf { calories != null }
    }

    LaunchedEffect(chartValues) {
        if(chartValues.isNotEmpty()){
            chartModelProducer.runTransaction {
                columnModel {
                    series(chartValues)
                }
            }
        }
    }

    LaunchedEffect(macroChartValues) {
        if(macroChartValues.isNotEmpty()){
            macroChartModelProducer.runTransaction {
                columnModel {
                    series(macroChartValues)
                }
            }
        }
    }

    LaunchedEffect(monthlyChartValues) {
        if(monthlyChartValues.isNotEmpty()){
            monthlyChartModelProducer.runTransaction {
                columnModel {
                    series(monthlyChartValues)
                }
            }
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
        .verticalScroll(rememberScrollState())) {
        Text(
            text = "Nutrition Analytics",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text("Today's Nutrition", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ){
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Calories: ${totalCalories?.let { "$it kcal"} ?: "Not available"}")
                Text("Protein: ${totalProtein?.let { "$it g"} ?: "Not available"}")
                Text("Carbohydrates: ${totalCarbs?.let { "$it g"} ?: "Not available"}")
                Text("Fat: ${totalFat?.let { "$it g"} ?: "Not available"}")
            }
        }
        Spacer(Modifier.height(24.dp))

        Text("Last 7 Days",style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))
/*        weeklyCalories.forEach { (date, calories) ->
            Text(
                text = "${date.dayOfMonth}/${date.monthValue}: ${
                    calories?.let { "${it.toInt()} kcal" } ?: "Not available"
                }"
            )
        }*/

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ){
            CartesianChartHost(
                chart = rememberCartesianChart(
                    rememberColumnCartesianLayer(
                        columnProvider = ColumnCartesianLayer.ColumnProvider.series(
                            rememberLineComponent(
                                fill = Fill(calorieGradient),
                                thickness = 18.dp,
                                shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                            )
                        )
                    ),
                    startAxis = VerticalAxis.rememberStart(),
                    bottomAxis = HorizontalAxis.rememberBottom(
                        valueFormatter = { _, x, _ ->
                            chartLabels.getOrNull(x.toInt()) ?: x.toInt().toString()
                        },
                        itemPlacer = remember { HorizontalAxis.ItemPlacer.aligned(spacing = {1}) }
                    )
                ),
                modelProducer = chartModelProducer,
                modifier = Modifier
                    .fillMaxWidth().padding(16.dp)
                    .height(260.dp)
            )
        }
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Daily average: ${
                weeklyAverage?.let { "${it.toInt()} kcal" } ?: "Not available"
            }",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "Weekly total: ${
                weeklyTotal?.let { "${it.toInt()} kcal" } ?: "Not available"
            }",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(Modifier.height(24.dp))

        Text(text="Last 30 Days",    style = MaterialTheme.typography.titleLarge)

        Spacer(Modifier.height(12.dp))
        Text(
            "Daily calorie intake over the past 30 days",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 3.dp
            )
        ){
            CartesianChartHost(
                chart = rememberCartesianChart(
                    rememberColumnCartesianLayer(
                        ColumnCartesianLayer.ColumnProvider.series(rememberLineComponent(
                            fill = Fill(calorieGradient),
                            thickness = 8.dp,
                            shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
                        ))
                    ),
                    startAxis = VerticalAxis.rememberStart(),
                    bottomAxis = HorizontalAxis.rememberBottom(
                        valueFormatter = {_ , x, _ ->
                            monthlyChartLabels.getOrNull(x.toInt()) ?: x.toInt().toString()
                        },
                        itemPlacer = remember {
                            HorizontalAxis.ItemPlacer.aligned(spacing = {5})
                        }
                    )
                ),
                modelProducer =monthlyChartModelProducer,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(260.dp)
            )
        }
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Daily average: ${
                monthlyAverage?.let { "${it.toInt()} kcal" } ?: "Not available"
            }",
            style = MaterialTheme.typography.bodyLarge
        )

        Text(
            text="30-day total: ${
                monthlyTotal?.let { "${it.toInt()} kcal" } ?: "Not available"
            }",
            style = MaterialTheme.typography.bodyLarge
        )


        Text(
            "Calories by Macronutrients",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(12.dp))
        Text(    "Estimated energy contribution (kcal)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(8.dp))

        if(macroChartValues.isNotEmpty()){
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                CartesianChartHost(
                    chart = rememberCartesianChart(
                        rememberColumnCartesianLayer(
                            columnProvider = ColumnCartesianLayer.ColumnProvider.series(
                                rememberLineComponent(
                                    fill = Fill(macroGradient),
                                    thickness = 18.dp,
                                    shape = RoundedCornerShape(
                                        topStart = 8.dp,
                                        topEnd = 8.dp
                                    )
                                )
                            ),
                        ),
                        startAxis = VerticalAxis.rememberStart(),
                        bottomAxis = HorizontalAxis.rememberBottom(
                            valueFormatter = { _, x, _ ->
                                listOf("Protein", "Carbs", "Fat").getOrNull(x.toInt()) ?: x.toInt().toString()
                            },
                            itemPlacer = remember { HorizontalAxis.ItemPlacer.aligned(spacing = { 1 }) }
                        )
                    ),
                    modelProducer = macroChartModelProducer,
                    modifier = Modifier
                        .fillMaxWidth().padding(16.dp)
                        .height(240.dp)
                )
            }
        } else {
            Text("Macro data is not available for all today's meals.")
        }

        Spacer(Modifier.height(24.dp))

        Text(
            "Daily Goal Progress",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(12.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 3.dp
            )
        ){
            Column(
                modifier = Modifier.padding(16.dp)
            ){
                NutritionGoalItem("Calories",
                    totalCalories,calorieTarget,"kcal")
                NutritionGoalItem("Protein",
                    totalProtein,proteinTarget,"g")
                NutritionGoalItem("Carbohydrates",
                    totalCalories,carbsTarget,"g")
                NutritionGoalItem("Fat",
                    totalFat,fatTarget,"g")
            }
        }

    }
}

@Composable
fun NutritionGoalItem(label: String, consumed: Double?, target: Double,unit: String){
    val progress = if (consumed != null && target > 0) {
        (consumed / target).toFloat().coerceIn(0f, 1f)
    } else {
        0f
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label)
            Text(
                if(consumed != null){
                    "${consumed.toInt()}/${target.toInt()} $unit"
                } else {
                    "Not available"
                }
            )
        }
        Spacer(Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = {progress},
            modifier = Modifier.fillMaxWidth()
        )
    }

}