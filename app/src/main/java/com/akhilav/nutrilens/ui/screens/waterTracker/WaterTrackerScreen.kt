 package com.akhilav.nutrilens.ui.screens.waterTracker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.remote.creation.toFloat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.akhilav.nutrilens.data.local.entity.WaterIntakeEntity
import com.akhilav.nutrilens.viewmodels.watertracker.WaterTrackerViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

 @Composable
fun WaterTrackerScreen(
    viewmodel: WaterTrackerViewModel = hiltViewModel()
) {
    val waterEntries: List<WaterIntakeEntity> by viewmodel.todayWaterIntake.collectAsStateWithLifecycle()
    val totalWater:Int by viewmodel.todayTotalWater.collectAsStateWithLifecycle()
    val dailyGoal by viewmodel.dailyWaterTarget.collectAsStateWithLifecycle()

    var customAmount by remember { mutableStateOf("") }
    var showGoalDialog by remember {mutableStateOf(false)}
    var goalInput by remember {mutableStateOf("")}
    val progress = if (dailyGoal>0){
        (totalWater.toFloat()/dailyGoal.toFloat()).coerceIn(0f,1f)
    } else {
        0f
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(16.dp)) {
        Text(text = "Water Tracker", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(20.dp))
        Card(
            modifier = Modifier.fillMaxWidth()
        ){
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text("Today's Water Intake")

                Spacer(modifier = Modifier.height(12.dp))

                Text(text = "$totalWater ml",style = MaterialTheme.typography.headlineLarge)
                Text("Daily goal: $dailyGoal ml")
                Spacer(modifier = Modifier.height(16.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))
                Text("${(progress * 100).toInt()}% completed")

                TextButton(onClick = {
                    goalInput = dailyGoal.toString()
                    showGoalDialog = true
                }) {                    Text("Edit Daily Goal")
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Add Water",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)){
            listOf(150,250,500).forEach { amount ->
                Button(onClick = {
                    viewmodel.addWater(amount)
                },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("+${amount}ml")
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = customAmount,
            onValueChange = { customAmount = it },
            label = { Text("Custom amount (ml)") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            customAmount.toIntOrNull()?.let { amount ->
                if(amount>0){
                    viewmodel.addWater(amount)
                    customAmount = ""
                }
            }
        },
            modifier = Modifier.fillMaxWidth(),
            enabled = (customAmount.toIntOrNull() ?: 0) > 0){
            Text("Add Custom Water")
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Today's History",
            style = MaterialTheme.typography.titleLarge
        )
        if(waterEntries.isEmpty()){
            Text(
                text = "No water intake recorded today.",
                modifier = Modifier.padding(vertical = 16.dp)
            )
        } else {
            waterEntries.forEach { entry ->
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)){
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text("${entry.amountMl} ml")
                            Text(SimpleDateFormat("hh:mm a", Locale.getDefault())
                                .format(Date(entry.consumedAt)),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        IconButton(onClick = {
                            viewmodel.deleteWaterIntake(entry)
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete water entry"
                            )
                        }
                    }
                }
            }
        }
    }

     if(showGoalDialog){
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
                     )
                 )
             },
             confirmButton = {
                TextButton(onClick = {
                    goalInput.toIntOrNull()?.let { goal ->
                        if(goal>0){
                            viewmodel.saveDailyWaterTarget(goal)
                            showGoalDialog = false
                        }
                    }
                }) {Text("Save") }
             },
             dismissButton = {
                TextButton(onClick = {
                    showGoalDialog = false
                }) { Text("Cancel")}
             }
         )
     }


}