package com.akhilav.nutrilens.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.akhilav.nutrilens.viewmodels.HomeNutritionViewModel
import java.time.LocalTime

val currentHour = LocalTime.now().hour

val greeting = when (currentHour) {
    in 5..11 -> "Good morning!"
    in 12..16 -> "Good afternoon!"
    in 17..20 -> "Good evening!"
    else -> "Good night!"
}
@Composable
fun HomeScreen(onScanClick:()-> Unit, viewModel: HomeNutritionViewModel = hiltViewModel()) {
    val nutritionSummary by viewModel.nutritionSummary.collectAsStateWithLifecycle()
    val dailyCalorieTarget by viewModel.dailyCaloriesTarget.collectAsStateWithLifecycle()
    val dailyProteinTarget by viewModel.dailyProteinTarget.collectAsStateWithLifecycle()
    val dailyCarbsTarget by viewModel.dailyCarbsTarget.collectAsStateWithLifecycle()
    val dailyFatTarget by viewModel.dailyFatTarget.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF087F5B),
                                Color(0xFF20A878),
                                Color(0xFF74C69D)
                            )
                        )
                    ).padding(22.dp)
            ) {
                Column {
                    // Welcome section
                    Text(
                        text = greeting,
                        color = Color.White,
                        fontSize = 27.sp,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Make every bite count.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    Surface(
                        shape = RoundedCornerShape(50.dp),
                        color = Color.White.copy(0.2f)
                    ) {
                        Text(
                            text = "Your daily health companion",
                            color = Color.White,
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(
                                horizontal = 14.dp,
                                vertical = 8.dp
                            )
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(26.dp))

        // Daily Progress
        SectionTitle(
            title = "Daily Nutrition",
            subtitle = "Your nutrition overview"
        )
        Spacer(modifier = Modifier.height(14.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Daily calorie intake",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = nutritionSummary?.calories?.toInt()?.toString() ?: "--",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.padding(start = 8.dp))
                    Column {
                        Text(
                            text = "kcal consumed",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Daily target: ${dailyCalorieTarget.toInt()} kcal",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                val calories = nutritionSummary?.calories
                val progress = if(calories != null && dailyCalorieTarget > 0){
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
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text=if (calories != null && dailyCalorieTarget > 0) {
                        "${(calories / dailyCalorieTarget * 100).toInt()}% of daily target"
                        }else {
                        "Progress will appear after tracking meals"
                        }, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        SectionTitle(title = "Macronutrients", subtitle = "Your protein, carbs and fat")
        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            NutritionCard(
                title = "Calories",
                value = "--",
                unit = "kcal",
                modifier = Modifier.weight(1f),
                color = Color(0xFFE8F1FF),
                textColor = Color(0xFF2563EB)
            )
            NutritionCard(
                title = "Protein",
                value = "--",
                unit = "g",
                modifier = Modifier.weight(1f),
                color = Color(0xFFFFF1DC),
                textColor = Color(0xFFD97706),
            )
            NutritionCard(
                title = "Fat",
                value = "--",
                unit = "g",
                color = Color(0xFFFFE8E8),
                textColor = Color(0xFFDC4444),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(26.dp))
        // Scanner action
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Know what you eat",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Scan a food barcode to explore its nutrition information.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onScanClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF087F5B)
                    )
                ) {
                    Text(
                        text = "Scan Food  →",
                        fontSize = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(26.dp))
        SectionTitle(title = "Nutrition Progress", subtitle = "Track your daily nutrition goals")
        Spacer(Modifier.height(14.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ){
                NutritionProgressIndicator("Calories", consumed = nutritionSummary?.calories, target=dailyCalorieTarget, unit="kcal",progressColor = Color(0xFF087F5B))
                NutritionProgressIndicator("Protein", consumed = nutritionSummary?.protein, target=dailyProteinTarget, unit="g",progressColor = Color(0xFF2563EB))
                NutritionProgressIndicator(
                    title = "Carbohydrates",
                    consumed = nutritionSummary?.carbohydrates,
                    target = dailyCarbsTarget,
                    unit = "g",
                    progressColor = Color(0xFFD97706)
                )

                NutritionProgressIndicator(
                    title = "Fat",
                    consumed = nutritionSummary?.fat,
                    target = dailyFatTarget,
                    unit = "g",
                    progressColor = Color(0xFFDC4444)
                )
            }
        }
        Spacer(modifier = Modifier.height(26.dp))
        // Recent scans section
        SectionTitle(title = "Recent Scans", subtitle = "Your recently scanned products")
        Spacer(modifier = Modifier.height(14.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🥗",
                        fontSize = 30.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "No scans yet",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Your scanned food products will appear here.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

    }
}

@Composable
private fun SectionTitle(title:String, subtitle:String){
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
@Composable
private fun NutritionCard(
    title: String, value:String, unit:String, modifier:Modifier = Modifier,color: Color,textColor: Color
){
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor=color)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(14.dp)
        ){
            Text(
                text = title,
                color = textColor,
                style = MaterialTheme.typography.titleSmall
            )

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = value,
                color = textColor,
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = unit,
                color = textColor.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(
                        textColor.copy(alpha = 0.15f),
                        CircleShape
                    )
            )
        }
    }
}

@Composable
private fun NutritionProgressIndicator(title: String, consumed: Double?,target: Double?,unit: String,progressColor: Color){
    val hasProgress = consumed != null && target != null && target > 0
    val progress = if(hasProgress){
        (consumed!! / target!!).toFloat().coerceIn(0f,1f)
    } else {
        0f
    }

    Column {
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall
            )

            Text(
                text = if (consumed != null) {
                    "${"%.1f".format(consumed)} $unit"
                } else {
                    "-- $unit"
                },
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(
                    MaterialTheme.colorScheme.surfaceVariant,
                    CircleShape
                )
        ){
            if (hasProgress) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(8.dp)
                        .background(progressColor, CircleShape)
                )
            }
        }

            Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = when {
                consumed == null -> "No tracking data yet"
                target == null || target <= 0 -> "Daily target not set"
                else -> "Target: ${"%.1f".format(target)} $unit"
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

    }


}