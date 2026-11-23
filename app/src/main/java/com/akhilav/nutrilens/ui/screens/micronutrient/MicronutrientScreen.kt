package com.akhilav.nutrilens.ui.screens.micronutrient

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.akhilav.nutrilens.ui.component.GlassCard
import com.akhilav.nutrilens.viewmodels.micro_nutrient.MicronutrientViewModel
import java.util.Locale

@Composable
fun MicronutrientScreen(
     micronutrientViewModel: MicronutrientViewModel = hiltViewModel()
){

    val microNutrients by micronutrientViewModel.todayMicronutrients.collectAsStateWithLifecycle()
    val items = listOf(
        Triple("Vitamin A", microNutrients.vitaminA, "µg"),
        Triple("Vitamin C", microNutrients.vitaminC, "mg"),
        Triple("Vitamin D", microNutrients.vitaminD, "µg"),
        Triple("Vitamin B12", microNutrients.vitaminB12, "µg"),
        Triple("Calcium", microNutrients.calcium, "mg"),
        Triple("Iron", microNutrients.iron, "mg"),
        Triple("Potassium", microNutrients.potassium, "mg"),
        Triple("Magnesium", microNutrients.magnesium, "mg"),
        Triple("Zinc", microNutrients.zinc, "mg")
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Text(
            text = "Micronutrients",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Today's nutrient intake",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                items.forEach { (name,value,unit) ->
                    Row(modifier =Modifier.fillMaxWidth().padding(vertical = 12.dp),horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
                        Text(text= name,
                            style = MaterialTheme.typography.bodyLarge)
                        Text(
                            text=value?.let {
                                String.format(Locale.getDefault(), "%.2f %s",it,unit)
                            } ?: "Not available",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    HorizontalDivider()
                }
            }
        }

    }

}