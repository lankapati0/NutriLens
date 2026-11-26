 package com.akhilav.nutrilens.ui.screens.waterTracker

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.akhilav.nutrilens.data.local.entity.WaterIntakeEntity
import com.akhilav.nutrilens.ui.theme.BrandCoral
import com.akhilav.nutrilens.ui.theme.BrandDeepTeal
import com.akhilav.nutrilens.ui.theme.BrandOceanTeal
import com.akhilav.nutrilens.ui.component.GlassCard
import com.akhilav.nutrilens.viewmodels.watertracker.WaterTrackerViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


 @Composable
 fun WaterTrackerScreen(
     viewmodel: WaterTrackerViewModel = hiltViewModel()
 ) {
     val waterEntries: List<WaterIntakeEntity> by viewmodel.todayWaterIntake.collectAsStateWithLifecycle()
     val weeklyEntries: List<WaterIntakeEntity> by viewmodel.weeklyWaterIntake.collectAsStateWithLifecycle()
     val totalWater: Int by viewmodel.todayTotalWater.collectAsStateWithLifecycle()
     val dailyGoal by viewmodel.dailyWaterTarget.collectAsStateWithLifecycle()

     var customAmount by remember { mutableStateOf("") }
     var showGoalDialog by remember { mutableStateOf(false) }
     var goalInput by remember { mutableStateOf("") }

     val progress = if (dailyGoal > 0) {
         (totalWater.toFloat() / dailyGoal.toFloat()).coerceIn(0f, 1f)
     } else {
         0f
     }

     Column(
         modifier = Modifier
             .fillMaxSize()
             .verticalScroll(rememberScrollState())
             .padding(16.dp),
         verticalArrangement = Arrangement.spacedBy(16.dp)
     ) {

         Text(
             text = "Water Tracker",
             style = MaterialTheme.typography.headlineMedium,
             color = MaterialTheme.colorScheme.onBackground
         )

         // Daily Water Progress
         GlassCard(modifier = Modifier.fillMaxWidth()) {
             Row(
                 modifier = Modifier
                     .fillMaxWidth()
                     .padding(20.dp),
                 verticalAlignment = Alignment.CenterVertically,
                 horizontalArrangement = Arrangement.spacedBy(20.dp)
             ) {
                 WaterBottleProgress(progress = progress)

                 Column(
                     modifier = Modifier.weight(1f),
                     verticalArrangement = Arrangement.spacedBy(10.dp)
                 ) {
                     Text(
                         text = "Today's Water Intake",
                         style = MaterialTheme.typography.titleMedium
                     )

                     Text(
                         text = "$totalWater ml",
                         style = MaterialTheme.typography.headlineMedium,
                         fontWeight = FontWeight.Bold,
                         color = BrandOceanTeal
                     )

                     Text(
                         text = "Daily goal: $dailyGoal ml",
                         style = MaterialTheme.typography.bodyMedium
                     )

                     LinearProgressIndicator(
                         progress = { progress },
                         modifier = Modifier
                             .fillMaxWidth()
                             .height(8.dp)
                             .clip(CircleShape),
                         color = BrandOceanTeal,
                         trackColor = MaterialTheme.colorScheme.surfaceVariant
                     )

                     Text(
                         text = "${(progress * 100).toInt()}% completed",
                         style = MaterialTheme.typography.labelMedium
                     )
                     Text(
                         text = when {
                             progress >= 1f -> "Daily water goal achieved! 🎉"
                             progress >= 0.75f -> "Almost there! Keep it up! 💧"
                             progress >= 0.5f -> "You're halfway to your goal! 🌊"
                             progress >= 0.25f -> "Great start! Keep drinking water. 💧"
                             else -> "Stay hydrated. Every glass counts! 💙"
                         },
                         style = MaterialTheme.typography.bodySmall,
                         color = BrandOceanTeal,
                         fontWeight = FontWeight.Medium
                     )
                     TextButton(
                         onClick = { showGoalDialog = true }
                     ) {
                         Text("Edit Daily Goal")
                     }
                 }
             }
         }

         WeeklyWaterChart(weeklyEntries,dailyGoal)

         // Add Water
         Text(
             text = "Add Water",
             style = MaterialTheme.typography.titleLarge,
             color = MaterialTheme.colorScheme.onBackground
         )


         GlassCard(modifier = Modifier.fillMaxWidth()) {
             Column(
                 modifier = Modifier
                     .fillMaxWidth()
                     .padding(16.dp),
                 verticalArrangement = Arrangement.spacedBy(16.dp)
             ) {
                 Text(
                     text = "Add Water",
                     style = MaterialTheme.typography.titleMedium,
                     fontWeight = FontWeight.Bold
                 )

                 Row(
                     modifier = Modifier.fillMaxWidth(),
                     horizontalArrangement = Arrangement.spacedBy(10.dp)
                 ) {
                     listOf(150, 250, 500).forEach { amount ->
                         Surface(
                             onClick = {
                                 viewmodel.addWater(amount)
                             },
                             modifier = Modifier.weight(1f),
                             shape = RoundedCornerShape(16.dp),
                             color = BrandOceanTeal.copy(alpha = 0.12f),
                             border = BorderStroke(
                                 1.dp,
                                 BrandOceanTeal.copy(alpha = 0.35f)
                             )
                         ) {
                             Column(
                                 modifier = Modifier.padding(vertical = 16.dp),
                                 horizontalAlignment = Alignment.CenterHorizontally,
                                 verticalArrangement = Arrangement.spacedBy(6.dp)
                             ) {
                                 Icon(
                                     imageVector = Icons.Outlined.WaterDrop,
                                     contentDescription = null,
                                     tint = BrandOceanTeal
                                 )

                                 Text(
                                     text = "${amount} ml",
                                     fontWeight = FontWeight.Bold,
                                     color = BrandOceanTeal
                                 )
                             }
                         }
                     }
                 }

                 OutlinedTextField(
                     value = customAmount,
                     onValueChange = { customAmount = it },
                     label = { Text("Custom amount (ml)") },
                     leadingIcon = {
                         Icon(
                             Icons.Outlined.WaterDrop,
                             contentDescription = null,
                             tint = BrandOceanTeal
                         )
                     },
                     keyboardOptions = KeyboardOptions(
                         keyboardType = KeyboardType.Number
                     ),
                     modifier = Modifier.fillMaxWidth(),
                     singleLine = true,
                     shape = RoundedCornerShape(16.dp)
                 )

                 Button(
                     onClick = {
                         customAmount.toIntOrNull()?.let { amount ->
                             if (amount > 0) {
                                 viewmodel.addWater(amount)
                                 customAmount = ""
                             }
                         }
                     },
                     modifier = Modifier
                         .fillMaxWidth()
                         .height(52.dp),
                     enabled = (customAmount.toIntOrNull() ?: 0) > 0,
                     shape = RoundedCornerShape(16.dp),
                     colors = ButtonDefaults.buttonColors(
                         containerColor = BrandOceanTeal,
                         contentColor = Color.White
                     )
                 ) {
                     Text(
                         text = "Add Custom Water",
                         fontWeight = FontWeight.SemiBold
                     )
                 }
             }
         }


         // Today's History
         Text(
             text = "Today's History",
             style = MaterialTheme.typography.titleLarge,
             color = MaterialTheme.colorScheme.onBackground
         )

         if (waterEntries.isEmpty()) {
             GlassCard(modifier = Modifier.fillMaxWidth()) {
                 Column(
                     modifier = Modifier
                         .fillMaxWidth()
                         .padding(24.dp),
                     horizontalAlignment = Alignment.CenterHorizontally,
                     verticalArrangement = Arrangement.spacedBy(8.dp)
                 ) {
                     Icon(
                         imageVector = Icons.Outlined.WaterDrop,
                         contentDescription = null,
                         tint = MaterialTheme.colorScheme.onSurfaceVariant,
                         modifier = Modifier.size(32.dp)
                     )

                     Text(
                         "No water intake recorded today.",
                         style = MaterialTheme.typography.bodyMedium,
                         color = MaterialTheme.colorScheme.onSurfaceVariant
                     )
                 }
             }
         } else {
             waterEntries.forEach { entry ->

                 GlassCard(modifier = Modifier.fillMaxWidth()) {
                     Row(
                         modifier = Modifier
                             .fillMaxWidth()
                             .padding(14.dp),
                         verticalAlignment = Alignment.CenterVertically,
                         horizontalArrangement = Arrangement.spacedBy(14.dp)
                     ) {
                         Box(
                             modifier = Modifier
                                 .size(46.dp)
                                 .background(
                                     BrandOceanTeal.copy(alpha = 0.12f),
                                     CircleShape
                                 ),
                             contentAlignment = Alignment.Center
                         ) {
                             Icon(
                                 imageVector = Icons.Outlined.WaterDrop,
                                 contentDescription = null,
                                 tint = BrandOceanTeal,
                                 modifier = Modifier.size(24.dp)
                             )
                         }

                         Column(
                             modifier = Modifier.weight(1f),
                             verticalArrangement = Arrangement.spacedBy(4.dp)
                         ) {
                             Text(
                                 text = "${entry.amountMl} ml",
                                 style = MaterialTheme.typography.titleMedium,
                                 fontWeight = FontWeight.Bold
                             )

                             Text(
                                 text = SimpleDateFormat(
                                     "hh:mm a",
                                     Locale.getDefault()
                                 ).format(Date(entry.consumedAt)),
                                 style = MaterialTheme.typography.bodySmall,
                                 color = MaterialTheme.colorScheme.onSurfaceVariant
                             )
                         }

                         IconButton(
                             onClick = {
                                 viewmodel.deleteWaterIntake(entry)
                             }
                         ) {
                             Icon(
                                 imageVector = Icons.Default.Delete,
                                 contentDescription = "Delete water entry",
                                 tint = MaterialTheme.colorScheme.error
                             )
                         }
                     }
                 }
             }
         }

         Spacer(modifier = Modifier.height(8.dp))
     }

     // Daily Goal Dialog
     if (showGoalDialog) {
         AlertDialog(
             onDismissRequest = {
                 showGoalDialog = false
             },
             title = {
                 Text("Daily Water Goal")
             },
             text = {
                 OutlinedTextField(
                     value = goalInput,
                     onValueChange = { goalInput = it },
                     label = { Text("Goal in ml") },
                     keyboardOptions = KeyboardOptions(
                         keyboardType = KeyboardType.Number
                     ),
                     singleLine = true
                 )
             },
             confirmButton = {
                 TextButton(
                     onClick = {
                         goalInput.toIntOrNull()?.let { goal ->
                             if (goal > 0) {
                                 viewmodel.saveDailyWaterTarget(goal)
                                 showGoalDialog = false
                             }
                         }
                     },
                     enabled = (goalInput.toIntOrNull() ?: 0) > 0
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
                         showGoalDialog = false
                     }
                 ) {
                     Text("Cancel")
                 }
             }
         )
     }
 }


 @Composable
 fun WaterBottleProgress(progress: Float) {
     val bottleShape = RoundedCornerShape(
         topStart = 24.dp,
         topEnd = 24.dp,
         bottomStart = 18.dp,
         bottomEnd = 18.dp
     )

     Column(
         horizontalAlignment = Alignment.CenterHorizontally
     ) {
         // Bottle neck
         Box(
             modifier = Modifier
                 .width(34.dp)
                 .height(24.dp)
                 .background(
                     BrandOceanTeal.copy(alpha = 0.25f),
                     RoundedCornerShape(
                         topStart = 8.dp,
                         topEnd = 8.dp
                     )
                 )
                 .border(
                     2.dp,
                     BrandOceanTeal,
                     RoundedCornerShape(
                         topStart = 8.dp,
                         topEnd = 8.dp
                     )
                 )
         )

         // Bottle body
         Box(
             modifier = Modifier
                 .width(94.dp)
                 .height(160.dp)
                 .clip(bottleShape)
                 .background(
                     MaterialTheme.colorScheme.surfaceVariant
                 )
                 .border(
                     3.dp,
                     BrandOceanTeal,
                     bottleShape
                 )
         ) {
             // Water level
             Box(
                 modifier = Modifier
                     .fillMaxWidth()
                     .fillMaxHeight(progress.coerceIn(0f, 1f))
                     .align(Alignment.BottomCenter)
                     .background(
                         Brush.verticalGradient(
                             colors = listOf(
                                 Color(0xFF64D8E5),
                                 BrandOceanTeal
                             )
                         )
                     )
             )

             // Bottle highlight
             Box(
                 modifier = Modifier
                     .align(Alignment.CenterStart)
                     .padding(start = 12.dp)
                     .width(5.dp)
                     .fillMaxHeight(0.75f)
                     .background(
                         Color.White.copy(alpha = 0.35f),
                         CircleShape
                     )
             )
         }
     }
 }
 @SuppressLint("RememberReturnType")

 @Composable
 fun WeeklyWaterChart(
     entries: List<WaterIntakeEntity>,
     dailyGoal: Int
 ) {
     val dayData = remember(entries) {
         val dateFormat = java.text.SimpleDateFormat(
             "yyyyMMdd",
             java.util.Locale.getDefault()
         )

         val labelFormat = java.text.SimpleDateFormat(
             "EEE",
             java.util.Locale.getDefault()
         )

         val totals = entries.groupBy {
             dateFormat.format(java.util.Date(it.consumedAt))
         }.mapValues { (_, items) ->
             items.sumOf { it.amountMl }
         }

         (6 downTo 0).map { daysAgo ->
             val calendar = java.util.Calendar.getInstance()
             calendar.add(
                 java.util.Calendar.DAY_OF_YEAR,
                 -daysAgo
             )

             val key = dateFormat.format(calendar.time)
             val label = labelFormat.format(calendar.time)
             val total = totals[key] ?: 0

             Triple(label, total, total >= dailyGoal)
         }
     }

     val maxValue = maxOf(
         dailyGoal,
         dayData.maxOfOrNull { it.second } ?: 0,
         1
     )

     GlassCard(modifier = Modifier.fillMaxWidth()) {
         Column(
             modifier = Modifier
                 .fillMaxWidth()
                 .padding(16.dp),
             verticalArrangement = Arrangement.spacedBy(16.dp)
         ) {
             Text(
                 text = "Weekly Water Intake",
                 style = MaterialTheme.typography.titleMedium,
                 fontWeight = FontWeight.Bold
             )

             Text(
                 text = "Daily goal: ${dailyGoal} ml",
                 style = MaterialTheme.typography.bodySmall,
                 color = MaterialTheme.colorScheme.onSurfaceVariant
             )

             Row(
                 modifier = Modifier
                     .fillMaxWidth()
                     .height(160.dp),
                 horizontalArrangement = Arrangement.spacedBy(8.dp),
                 verticalAlignment = Alignment.Bottom
             ) {
                 dayData.forEach { (day, amount, achieved) ->
                     Column(
                         modifier = Modifier.weight(1f),
                         horizontalAlignment = Alignment.CenterHorizontally,
                         verticalArrangement = Arrangement.Bottom
                     ) {
                         Text(
                             text = if (amount > 0) "$amount" else "0",
                             style = MaterialTheme.typography.labelSmall,
                             maxLines = 1
                         )

                         Box(
                             modifier = Modifier
                                 .fillMaxWidth()
                                 .height(110.dp)
                                 .padding(horizontal = 4.dp)
                                 .clip(RoundedCornerShape(8.dp))
                                 .background(
                                     MaterialTheme.colorScheme.surfaceVariant
                                 ),
                             contentAlignment = Alignment.BottomCenter
                         ) {
                             Box(
                                 modifier = Modifier
                                     .fillMaxWidth()
                                     .fillMaxHeight(
                                         (amount.toFloat() / maxValue)
                                             .coerceIn(0f, 1f)
                                     )
                                     .background(
                                         if (achieved) BrandDeepTeal
                                         else BrandOceanTeal
                                     )
                             )
                         }

                         Text(
                             text = day,
                             style = MaterialTheme.typography.labelSmall,
                             fontWeight = FontWeight.Medium
                         )
                     }
                 }
             }

             Text(
                 text = "💧 Days with achieved goals are shown in dark teal.",
                 style = MaterialTheme.typography.bodySmall,
                 color = MaterialTheme.colorScheme.onSurfaceVariant
             )
         }
     }
 }
