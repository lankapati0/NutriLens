package com.akhilav.nutrilens.viewmodels.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.Bitmap
import com.akhilav.nutrilens.data.repository.FoodConsumptionRepository
import com.akhilav.nutrilens.data.repository.ReportExportManager
import com.akhilav.nutrilens.data.repository.WaterIntakeRepository
import com.akhilav.nutrilens.utils.ReportChartRenderer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val foodConsumptionRepository: FoodConsumptionRepository,
    private val waterIntakeRepository: WaterIntakeRepository,
    private val reportExportManager: ReportExportManager
) : ViewModel() {

    private val _exportedPdf = MutableStateFlow<File?>(null)
    val exportedPdf: StateFlow<File?> = _exportedPdf

    private val _exportedCsv = MutableStateFlow<File?>(null)
    val exportedCsv: StateFlow<File?> = _exportedCsv

    val dailyNutritionSummary =
        foodConsumptionRepository.getTodayNutritionSummary()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                null
            )

    val dailyConsumptions =
        foodConsumptionRepository.getTodayConsumptions()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    val weeklyConsumptions =
        foodConsumptionRepository.getConsumptionsForLastDays(7)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    val monthlyConsumptions =
        foodConsumptionRepository.getConsumptionsForLastDays(30)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    val todayWaterTotal =
        waterIntakeRepository.getTodayTotalWater()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                0
            )

    fun generateReportCharts(): List<Pair<String, Bitmap>> {
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone)

        val daily = dailyConsumptions.value
        val weekly = weeklyConsumptions.value
        val monthly = monthlyConsumptions.value

        val weeklyData = (6 downTo 0).map { daysAgo ->
            val date = today.minusDays(daysAgo.toLong())

            val records = weekly.filter {
                Instant.ofEpochMilli(it.consumedAt)
                    .atZone(zone).toLocalDate() == date
            }

            val calories = when {
                records.isEmpty() -> 0.0
                records.any { it.calories == null } -> null
                else -> records.sumOf { it.calories!! }
            }

            date.dayOfWeek.getDisplayName(
                TextStyle.SHORT, Locale.getDefault()
            ) to calories
        }

        val monthlyData = (20 downTo 0).map { daysAgo ->
            val date = today.minusDays(daysAgo.toLong())

            val records = monthly.filter {
                Instant.ofEpochMilli(it.consumedAt).atZone(zone).toLocalDate() == date
            }

            val calories = when {
                records.isEmpty() -> 0.0
                records.any { it.calories == null } -> null
                else -> records.sumOf { it.calories!! }
            }
            date.dayOfMonth.toString() to calories
        }

        val protein = if (daily.isNotEmpty() && daily.all { it.protein != null }) {
            daily.sumOf { it.protein!! } * 4
        } else null

        val carbs = if (daily.isNotEmpty() && daily.all { it.carbohydrates != null }) {
            daily.sumOf { it.carbohydrates!! } * 4
        } else null

        val fat = if (daily.isNotEmpty() && daily.all { it.fat != null }) {
            daily.sumOf { it.fat!! } * 9
        } else null

        val renderer = ReportChartRenderer()

        return buildList {

            add(
                "Weekly Calories" to renderer.drawBarChart(
                    "",
                    weeklyData.filter { it.second != null }.map { it.first },
                    weeklyData.mapNotNull { it.second }
                )
            )

            add(
                "Monthly Calories" to renderer.drawBarChart(
                    "",
                    monthlyData.filter { it.second != null }.map { it.first },
                    monthlyData.mapNotNull { it.second }
                )
            )
            if (protein != null && carbs != null && fat != null) {
                add(
                    "Macronutrient Contribution" to renderer.drawBarChart(
                        "Macronutrient Contribution",
                        listOf("Protein", "Carbs", "Fat"),
                        listOf(protein, carbs, fat)
                    )
                )
            }
        }
    }

    fun exportPdfReport(){
        viewModelScope.launch(Dispatchers.IO){
            val entries = dailyConsumptions.value

            fun calculateTotal(values: List<Double?>): String {
                return when {
                    entries.isEmpty() -> "No entries"
                    values.any { it == null } -> "Not available"
                    else -> "${values.sumOf { it!! }.toInt()}"
                }
            }

            val reportContent = """
            DAILY NUTRITION REPORT

            Date: ${LocalDate.now()}
            
            Calories: ${calculateTotal(entries.map { it.calories })} kcal
            Protein: ${calculateTotal(entries.map { it.protein })} g
            Carbohydrates: ${calculateTotal(entries.map { it.carbohydrates })} g
            Fat: ${calculateTotal(entries.map { it.fat })} g


            Water Intake: ${todayWaterTotal.value} ml
            Food Entries: ${entries.size}
            
            WEEKLY REPORT
            Food Entries: ${weeklyConsumptions.value.size}
            
            MONTHLY REPORT
            Food Entries: ${monthlyConsumptions.value.size}
            """.trimIndent()

            val charts = generateReportCharts()

            val file = reportExportManager.exportPDFWithCharts(
                title = "NutriLens Nutrition Report",
                content = reportContent,
                charts = charts
            )

            _exportedPdf.value = file
        }
    }

    fun exportCsvReport(){
        viewModelScope.launch(Dispatchers.IO){
            val file = reportExportManager.exportCsv(dailyConsumptions.value)
            _exportedCsv.value = file
        }
    }

}