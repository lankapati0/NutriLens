 package com.akhilav.nutrilens.ui.screens.waterTracker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
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
import java.util.Date
import java.util.Locale


 @Composable
 fun WaterTrackerScreen(
     viewmodel: WaterTrackerViewModel = hiltViewModel()
 ) {
     val waterEntries: List<WaterIntakeEntity> by viewmodel.todayWaterIntake.collectAsStateWithLifecycle()
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
             Column(
                 modifier = Modifier
                     .fillMaxWidth()
                     .padding(20.dp),
                 horizontalAlignment = Alignment.CenterHorizontally,
                 verticalArrangement = Arrangement.spacedBy(12.dp)
             ) {
                 Icon(
                     imageVector = Icons.Outlined.WaterDrop,
                     contentDescription = null,
                     tint = BrandOceanTeal,
                     modifier = Modifier.size(36.dp)
                 )

                 Text(
                     "Today's Water Intake",
                     style = MaterialTheme.typography.titleMedium,
                     color = MaterialTheme.colorScheme.onSurfaceVariant
                 )

                 Text(
                     "$totalWater ml",
                     style = MaterialTheme.typography.headlineLarge,
                     color = BrandOceanTeal
                 )

                 Text(
                     "Daily goal: $dailyGoal ml",
                     style = MaterialTheme.typography.bodyMedium,
                     color = MaterialTheme.colorScheme.onSurfaceVariant
                 )

                 LinearProgressIndicator(
                     progress = { progress },
                     modifier = Modifier
                         .fillMaxWidth()
                         .height(10.dp),
                     color = BrandOceanTeal,
                     trackColor = MaterialTheme.colorScheme.surfaceVariant
                 )

                 Text(
                     "${(progress * 100).toInt()}% completed",
                     style = MaterialTheme.typography.labelLarge,
                     color = MaterialTheme.colorScheme.onSurface
                 )

                 TextButton(
                     onClick = {
                         goalInput = dailyGoal.toString()
                         showGoalDialog = true
                     }
                 ) {
                     Text(
                         "Edit Daily Goal",
                         color = BrandOceanTeal
                     )
                 }
             }
         }

         // Add Water
         Text(
             text = "Add Water",
             style = MaterialTheme.typography.titleLarge,
             color = MaterialTheme.colorScheme.onBackground
         )

         GlassCard(modifier = Modifier.fillMaxWidth()) {
             Column(
                 modifier = Modifier.padding(16.dp),
                 verticalArrangement = Arrangement.spacedBy(12.dp)
             ) {
                 Row(
                     modifier = Modifier.fillMaxWidth(),
                     horizontalArrangement = Arrangement.spacedBy(8.dp)
                 ) {
                     listOf(150, 250, 500).forEach { amount ->
                         Button(
                             onClick = {
                                 viewmodel.addWater(amount)
                             },
                             modifier = Modifier.weight(1f),
                             colors = ButtonDefaults.buttonColors(
                                 containerColor = BrandDeepTeal,
                                 contentColor = Color.White
                             )
                         ) {
                             Text("+${amount}ml")
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
                     singleLine = true
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
                     modifier = Modifier.fillMaxWidth(),
                     enabled = (customAmount.toIntOrNull() ?: 0) > 0,
                     colors = ButtonDefaults.buttonColors(
                         containerColor = BrandOceanTeal,
                         contentColor = Color.White
                     )
                 ) {
                     Text("Add Custom Water")
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
                 GlassCard(
                     modifier = Modifier.fillMaxWidth()
                 ) {
                     Row(
                         modifier = Modifier
                             .fillMaxWidth()
                             .padding(16.dp),
                         horizontalArrangement = Arrangement.SpaceBetween,
                         verticalAlignment = Alignment.CenterVertically
                     ) {
                         Row(
                             verticalAlignment = Alignment.CenterVertically,
                             horizontalArrangement = Arrangement.spacedBy(12.dp)
                         ) {
                             Icon(
                                 imageVector = Icons.Outlined.WaterDrop,
                                 contentDescription = null,
                                 tint = BrandOceanTeal,
                                 modifier = Modifier.size(28.dp)
                             )

                             Column {
                                 Text(
                                     "${entry.amountMl} ml",
                                     style = MaterialTheme.typography.titleMedium,
                                     color = MaterialTheme.colorScheme.onSurface
                                 )

                                 Text(
                                     SimpleDateFormat(
                                         "hh:mm a",
                                         Locale.getDefault()
                                     ).format(Date(entry.consumedAt)),
                                     style = MaterialTheme.typography.bodySmall,
                                     color = MaterialTheme.colorScheme.onSurfaceVariant
                                 )
                             }
                         }

                         IconButton(
                             onClick = {
                                 viewmodel.deleteWaterIntake(entry)
                             }
                         ) {
                             Icon(
                                 imageVector = Icons.Default.Delete,
                                 contentDescription = "Delete water entry",
                                 tint = BrandCoral
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
