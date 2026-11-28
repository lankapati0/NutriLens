package com.akhilav.nutrilens.ui.screens.sleep_track

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.akhilav.nutrilens.data.local.entity.SleepEntity
import com.akhilav.nutrilens.ui.component.AppToastType
import com.akhilav.nutrilens.ui.component.LocalAppToast
import com.akhilav.nutrilens.ui.theme.BrandCoral
import com.akhilav.nutrilens.ui.theme.BrandDeepTeal
import com.akhilav.nutrilens.ui.theme.BrandMint
import com.akhilav.nutrilens.ui.theme.BrandOceanTeal
import com.akhilav.nutrilens.viewmodels.sleep_tracker.SleepViewModel
import java.time.LocalDate
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SleepTrackScreen(
    onSleepDetailClick:()-> Unit = {},
    sleepViewModel: SleepViewModel = hiltViewModel()
) {
    val appToast = LocalAppToast.current
    val colorScheme = MaterialTheme.colorScheme
    val sleepRecords by sleepViewModel.sleepRecords.collectAsStateWithLifecycle()
    val latestSleep = sleepRecords.firstOrNull()
    var showAddSleepDialog by rememberSaveable { mutableStateOf(false) }
    var editingSleep by remember {mutableStateOf<SleepEntity?>(null)}
    val addSleepSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var showSleepDatePicker by remember {
        mutableStateOf(false)
    }
    val sleepDatePickerState = rememberDatePickerState()
    var showBedtimePicker by remember {
        mutableStateOf(false)
    }

    var showWakeUpPicker by remember {
        mutableStateOf(false)
    }

    val bedtimePickerState = rememberTimePickerState(
        initialHour = 23,
        initialMinute = 0
    )

    val wakeUpPickerState = rememberTimePickerState(
        initialHour = 7,
        initialMinute = 0
    )


    var sleepDate by rememberSaveable { mutableStateOf("") }
    var sleepBedtime by rememberSaveable { mutableStateOf("") }
    var sleepWakeUpTime by rememberSaveable { mutableStateOf("") }

    var sleepTotalMinutes by rememberSaveable { mutableStateOf("") }
    var sleepAwakeMinutes by rememberSaveable { mutableStateOf("24") }
    var sleepRemMinutes by rememberSaveable { mutableStateOf("94") }
    var sleepLightMinutes by rememberSaveable { mutableStateOf("270") }
    var sleepDeepMinutes by rememberSaveable { mutableStateOf("68") }

    val remainingStageMinutes = (sleepTotalMinutes.toIntOrNull() ?: 0) - (sleepRemMinutes.toIntOrNull() ?: 0) - (sleepLightMinutes.toIntOrNull() ?: 0) - (sleepDeepMinutes.toIntOrNull() ?: 0)

    var newSleepScore by rememberSaveable { mutableStateOf("82") }
    var newSleepGoal by rememberSaveable { mutableStateOf("480") }
    val totalSleepText = latestSleep?.let {
        formatSleepDuration(it.totalSleepMinutes)
    } ?: "7h 12m"
    val deepSleepText = latestSleep?.let {
        formatSleepDuration(it.deepMinutes)
    } ?: "1h 08m"
    val deepSleepPercentage = latestSleep?.let {
        if (it.totalSleepMinutes > 0) {
            (it.deepMinutes * 100) / it.totalSleepMinutes
        } else {
            0
        }
    } ?: 15
    val sleepScore = latestSleep?.sleepScore ?: 82
    val sleepGoalText = latestSleep?.let {
        formatSleepDuration(it.sleepGoalMinutes)
    } ?: "8h 00m"
    var selectedPeriod by rememberSaveable { mutableStateOf("Day") }


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
      item{
          Column(verticalArrangement = Arrangement.spacedBy(18.dp)){
              Text(
                  text = "Sleep",
                  style = MaterialTheme.typography.headlineMedium,
                  fontWeight = FontWeight.Bold,
                  color = colorScheme.onBackground
              )
              Button(
                  onClick = {
                      editingSleep = null

                      sleepDate = ""
                      sleepBedtime = ""
                      sleepWakeUpTime = ""

                      sleepTotalMinutes = ""
                      sleepAwakeMinutes = "24"
                      sleepRemMinutes = "94"
                      sleepLightMinutes = "270"
                      sleepDeepMinutes = "68"

                      newSleepScore = "82"
                      newSleepGoal = "480"

                      showAddSleepDialog = true
                  }, modifier = Modifier.fillMaxWidth(),
                  colors = ButtonDefaults.buttonColors(
                      containerColor = BrandDeepTeal
                  )
              ) {
                  Icon(
                      imageVector = Icons.Default.Add,
                      contentDescription = null
                  )

                  Spacer(modifier = Modifier.width(8.dp))
                  Text("Add Sleep Record")}
              Row(
                  modifier = Modifier
                      .fillMaxWidth()
                      .clip(RoundedCornerShape(50))
                      .background(colorScheme.surfaceVariant)
                      .padding(4.dp),
                  horizontalArrangement = Arrangement.spacedBy(4.dp)
              ){
                  listOf("Day","Week","Month","Year").forEach { period ->
                      Box(
                          modifier = Modifier
                              .weight(1f)
                              .clip(RoundedCornerShape(50))
                              .background(
                                  if (selectedPeriod == period)
                                      colorScheme.surface
                                  else
                                      Color.Transparent
                              )
                              .clickable {
                                  selectedPeriod = period
                              }
                              .padding(vertical = 10.dp),
                          contentAlignment = Alignment.Center
                      ){
                          Text(
                              text = period,
                              style = MaterialTheme.typography.labelMedium,
                              fontWeight = if (selectedPeriod == period)
                                  FontWeight.Bold
                              else
                                  FontWeight.Normal,
                              color = colorScheme.onSurface
                          )
                      }
                  }
              }
          }
      }

        item {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .clickable{onSleepDetailClick()},
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ){
                SleepScoreRing(totalSleepMinutes = latestSleep?.totalSleepMinutes ?: 432,
                    goalMinutes = latestSleep?.sleepGoalMinutes ?: 480,
                    sleepScore = sleepScore)

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ){
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(BrandOceanTeal, CircleShape)
                        )
                        Text(
                            text = "Time asleep 96%",
                            style = MaterialTheme.typography.labelSmall,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ){
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(BrandCoral, CircleShape)
                        )
                        Text(
                            text = "Quality $sleepScore",
                            style = MaterialTheme.typography.labelSmall,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                }
                Text(
                    text = "Good sleep, a little short",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onBackground
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ){
                SleepSummaryCard(
                    title = "Time Asleep",
                    value = totalSleepText,
                    subtitle = "of $sleepGoalText goal",
                    modifier = Modifier.weight(1f)
                )

                SleepSummaryCard(
                    title = "Deep Sleep",
                    value = deepSleepText,
                    subtitle = "$deepSleepPercentage of total",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item{ Spacer(modifier = Modifier.height(2.dp))
            SleepGoalProgress(    totalSleepMinutes = latestSleep?.totalSleepMinutes ?: 432,
                goalMinutes = latestSleep?.sleepGoalMinutes ?: 480
            )}

        item {
            SleepStagesTimeline(awakeMinutes = latestSleep?.awakeMinutes ?: 24,
                remMinutes = latestSleep?.remMinutes ?: 94,
                lightMinutes = latestSleep?.lightMinutes ?: 270,
                deepMinutes = latestSleep?.deepMinutes ?: 68)
        }

        if(selectedPeriod != "Day"){
            item{
                WeeklySleepChart(selectedPeriod,sleepRecords)
            }
        }

        item {
            SleepHistorySection(sleepRecords, onDelete = { sleepId ->
                sleepViewModel.deleteSleepRecord(sleepId)
            }, onUpdate = { sleep->
                sleepViewModel.updateSleepRecord(sleep)
            },onDeleteAll = {
                sleepViewModel.deleteAllSleepRecord()
            }, onEdit = {sleep ->
                editingSleep = sleep
                sleepDate = sleep.date
                sleepDatePickerState.selectedDateMillis = LocalDate.parse(sleep.date)
                    .atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
                sleepBedtime = sleep.bedtime
                sleep.bedtime.split(":").let {parts ->
                    bedtimePickerState.hour = parts[0].toInt()
                    bedtimePickerState.minute = parts[1].toInt()
                }
                sleep.wakeUpTime.split(":").let { parts ->
                    wakeUpPickerState.hour = parts[0].toInt()
                    wakeUpPickerState.minute = parts[1].toInt()
                }
                sleepWakeUpTime = sleep.wakeUpTime
                sleepTotalMinutes = sleep.totalSleepMinutes.toString()
                sleepAwakeMinutes = sleep.awakeMinutes.toString()
                sleepRemMinutes = sleep.remMinutes.toString()
                sleepLightMinutes = sleep.lightMinutes.toString()
                sleepDeepMinutes = sleep.deepMinutes.toString()
                newSleepScore = sleep.sleepScore.toString()
                newSleepGoal = sleep.sleepGoalMinutes.toString()
                showAddSleepDialog = true
            }
            )
        }

    }

    if (showAddSleepDialog) {

        ModalBottomSheet(onDismissRequest = {showAddSleepDialog = false},
            sheetState = addSleepSheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            shape = RoundedCornerShape(
                topStart = 32.dp,
                topEnd = 32.dp
            ),
            dragHandle = null) {
            var sleepFormError by rememberSaveable { mutableStateOf(false)}
            var sleepToastMessage by remember { mutableStateOf<String?>(null) }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.92f)
                    .padding(horizontal = 20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Header

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if(editingSleep == null){
                                    "Add Sleep Record"
                                } else {
                                    "Edit Sleep Record"
                                },
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Track your sleep and improve your rest",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(
                            onClick = {
                                showAddSleepDialog = false
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    //Sleep Schedule
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Sleep Schedule",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Box(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = sleepDate.takeIf { it.isNotBlank() }?.let{
                                    formatSleepDate(it)
                                } ?: "",
                                onValueChange = {},
                                label = { Text("Sleep Date") },
                                placeholder = { Text("Select date") },
                                modifier = Modifier.fillMaxWidth(),
                                readOnly = true,
                                enabled = false,
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = "Choose date"
                                    )
                                },
                                singleLine = true
                            )

                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clickable {
                                        showSleepDatePicker = true
                                    }
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                OutlinedTextField(
                                    value = sleepBedtime,
                                    onValueChange = {},
                                    label = { Text("Bedtime") },
                                    placeholder = { Text("Select time") },
                                    modifier = Modifier.fillMaxWidth(),
                                    readOnly = true,
                                    enabled = false,
                                    trailingIcon = {
                                        Icon(Icons.Default.AccessTime, contentDescription = null)
                                    },
                                    singleLine = true
                                )

                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .clickable { showBedtimePicker = true }
                                )
                            }

                            Box(modifier = Modifier.weight(1f)) {
                                OutlinedTextField(
                                    value = sleepWakeUpTime,
                                    onValueChange = {},
                                    label = { Text("Wake-up") },
                                    placeholder = { Text("Select time") },
                                    modifier = Modifier.fillMaxWidth(),
                                    readOnly = true,
                                    enabled = false,
                                    trailingIcon = {
                                        Icon(Icons.Default.AccessTime, contentDescription = null)
                                    },
                                    singleLine = true
                                )

                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .clickable { showWakeUpPicker = true }
                                )
                            }
                        }
                    }

                    // Sleep Duration
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Sleep Duration",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        OutlinedTextField(
                            value = sleepTotalMinutes.toIntOrNull()?.let{
                                formatSleepDuration(it)
                            } ?: "",
                            onValueChange = {},
                            label = { Text("Total sleep (minutes)") },
                            placeholder = { Text("Example: 432") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Bedtime,
                                    contentDescription = null
                                )
                            },
                            supportingText = {
                                Text("Example: 7 hours 12 minutes = 432 minutes")
                            }
                        )
                    }

                    // Sleep Stages
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Sleep Stages",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "Enter stage durations in minutes. REM + Light + Deep should equal Total Sleep.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        OutlinedTextField(
                            value = sleepAwakeMinutes,
                            onValueChange = { if (it.all(Char::isDigit)) sleepAwakeMinutes = it },
                            label = { Text("Awake (minutes)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        OutlinedTextField(
                            value = sleepRemMinutes,
                            onValueChange = { if (it.all(Char::isDigit)) sleepRemMinutes = it },
                            label = { Text("REM sleep (minutes)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        OutlinedTextField(
                            value = sleepLightMinutes,
                            onValueChange = { if (it.all(Char::isDigit)) sleepLightMinutes = it },
                            label = { Text("Light sleep (minutes)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        OutlinedTextField(
                            value = sleepDeepMinutes,
                            onValueChange = { if (it.all(Char::isDigit)) sleepDeepMinutes = it },
                            label = { Text("Deep sleep (minutes)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        Text(
                            text = when {
                                remainingStageMinutes > 0 ->
                                    "${formatSleepDuration(remainingStageMinutes)} still needs to be assigned to sleep stages."

                                remainingStageMinutes < 0 ->
                                    "Sleep stages exceed Total Sleep by ${formatSleepDuration(-remainingStageMinutes)}."

                                else ->
                                    "Sleep stage durations match Total Sleep."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = if (remainingStageMinutes == 0) {
                                BrandOceanTeal
                            } else {
                                MaterialTheme.colorScheme.error
                            }
                        )

                        Button(onClick = {
                            val total = sleepTotalMinutes.toIntOrNull() ?: 0

                            val rem = sleepRemMinutes.toIntOrNull() ?: 0
                            val light = sleepLightMinutes.toIntOrNull() ?: 0
                            val deep = sleepDeepMinutes.toIntOrNull() ?: 0
                            val currentStageTotal = rem + light + deep
                            if (total > 0 && currentStageTotal > 0) {
                                val newRem = total * rem / currentStageTotal
                                val newLight = total * light / currentStageTotal
                                val newDeep = total - newRem - newLight

                                sleepRemMinutes = newRem.toString()
                                sleepLightMinutes = newLight.toString()
                                sleepDeepMinutes = newDeep.toString()
                            }
                        },
                            modifier = Modifier.fillMaxWidth(),colors = ButtonDefaults.buttonColors(
                                containerColor = BrandDeepTeal,
                                contentColor = Color.White
                            )) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Auto-distribute Sleep Stages")
                        }
                        Text(
                            text = "Estimated values only. Actual sleep stages require data from a compatible sleep tracker.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Sleep Score & Goal
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Sleep Goals",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        OutlinedTextField(
                            value = newSleepScore,
                            onValueChange = {
                                if (it.all(Char::isDigit) && (it.isEmpty() || it.toInt() <= 100)) {
                                    newSleepScore = it
                                }
                            },
                            label = { Text("Sleep Score (0–100)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null
                                )
                            }
                        )

                        OutlinedTextField(
                            value = newSleepGoal,
                            onValueChange = {
                                if (it.all(Char::isDigit)) {
                                    newSleepGoal = it
                                }
                            },
                            label = { Text("Sleep Goal (minutes)") },
                            placeholder = { Text("Example: 480") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Flag,
                                    contentDescription = null
                                )
                            },
                            supportingText = {
                                Text("Example: 8 hours = 480 minutes")
                            }
                        )
                    }

                }
            }

            Button(
                onClick = {
                    val total = sleepTotalMinutes.toIntOrNull()
                    val awake = sleepAwakeMinutes.toIntOrNull()
                    val rem = sleepRemMinutes.toIntOrNull()
                    val light = sleepLightMinutes.toIntOrNull()
                    val deep = sleepDeepMinutes.toIntOrNull()
                    val score = newSleepScore.toIntOrNull()
                    val goal = newSleepGoal.toIntOrNull()


                    val isValid =
                        sleepDate.isNotBlank() &&
                                sleepBedtime.isNotBlank() &&
                                sleepWakeUpTime.isNotBlank() &&
                                total != null && total > 0 &&
                                awake != null && awake >= 0 &&
                                rem != null && rem >= 0 &&
                                light != null && light >= 0 &&
                                deep != null && deep >= 0 &&
                                score != null && score in 0..100 &&
                                goal != null && goal > 0 &&
                                rem + light + deep == total

                    val existingSleep = editingSleep
                    if(isValid){
                        sleepFormError = false
                        if(existingSleep == null){
                            sleepViewModel.addSleepRecord(
                                SleepEntity(date = sleepDate,
                                    bedtime = sleepBedtime,
                                    wakeUpTime = sleepWakeUpTime,
                                    totalSleepMinutes = total,
                                    awakeMinutes = awake,
                                    remMinutes = rem,
                                    lightMinutes = light,
                                    deepMinutes = deep,
                                    sleepScore = score,
                                    sleepGoalMinutes = goal)
                            )
                        } else {
                            sleepViewModel.updateSleepRecord(
                                existingSleep.copy(
                                    date = sleepDate,
                                    bedtime = sleepBedtime,
                                    wakeUpTime = sleepWakeUpTime,
                                    totalSleepMinutes = total,
                                    awakeMinutes = awake,
                                    remMinutes = rem,
                                    lightMinutes = light,
                                    deepMinutes = deep,
                                    sleepScore = score,
                                    sleepGoalMinutes = goal
                                )
                            )
                        }
                        editingSleep = null
                        showAddSleepDialog = false
                        appToast.show(
                            message = if(existingSleep == null){
                                "Sleep record saved successfully!"
                            } else {
                                "Sleep record updated successfully!"
                            },
                            type = AppToastType.SUCCESS
                        )
                    } else {
                        sleepFormError = true
                        appToast.show(
                            message = "Please check your entries. Sleep stages must equal Total Sleep.",
                            type = AppToastType.WARNING
                        )
                     }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandDeepTeal
                ),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(text = "Save Sleep Record", fontWeight = FontWeight.Bold)
            }
        }
    }

    if(showSleepDatePicker){
        DatePickerDialog(
            onDismissRequest = {showSleepDatePicker = false},
            confirmButton = {
                TextButton(
                    onClick = {
                        sleepDatePickerState.selectedDateMillis?.let { millis ->
                            sleepDate = java.time.Instant
                                .ofEpochMilli(millis)
                                .atZone(java.time.ZoneOffset.UTC)
                                .toLocalDate()
                                .toString()
                        }

                        showSleepDatePicker = false
                    }
                ) {
                    Text("Select")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showSleepDatePicker = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = sleepDatePickerState)
        }
    }


    if (showBedtimePicker) {
        TimePickerDialog(
            onCancel = {
                showBedtimePicker = false
            },
            onConfirm = {
                sleepBedtime = String.format(
                    java.util.Locale.getDefault(),
                    "%02d:%02d",
                    bedtimePickerState.hour,
                    bedtimePickerState.minute
                )
                calculateSleepDuration(sleepBedtime,sleepWakeUpTime)?.let {
                    sleepTotalMinutes = it.toString()
                }
                showBedtimePicker = false
            }
        ) {
            TimePicker(
                state = bedtimePickerState
            )
        }
    }

    if (showWakeUpPicker) {
        TimePickerDialog(
            onCancel = {
                showWakeUpPicker = false
            },
            onConfirm = {
                sleepWakeUpTime = String.format(
                    java.util.Locale.getDefault(),
                    "%02d:%02d",
                    wakeUpPickerState.hour,
                    wakeUpPickerState.minute
                )
                calculateSleepDuration(sleepBedtime,sleepWakeUpTime)?.let {
                    sleepTotalMinutes = it.toString()
                }
                showWakeUpPicker = false
            }
        ) {
            TimePicker(
                state = wakeUpPickerState
            )
        }
    }
}

@Composable
fun SleepSummaryCard(title:String, value:String, subtitle: String,modifier: Modifier = Modifier){
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(3.dp)
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ){
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = BrandOceanTeal
            )
        }
    }


}
@Composable
fun SleepStagesTimeline(awakeMinutes:Int, remMinutes:Int, lightMinutes:Int, deepMinutes:Int) {
    val stages = listOf(
        Triple("Awake", awakeMinutes, BrandCoral),
        Triple("REM", remMinutes, BrandMint),
        Triple("Light", lightMinutes, BrandOceanTeal),
        Triple("Deep", deepMinutes, BrandDeepTeal)
    )
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Sleep Stages",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val blocks = listOf(
                    1 to BrandDeepTeal,
                    2 to BrandOceanTeal,
                    1 to BrandMint,
                    3 to BrandOceanTeal,
                    2 to BrandDeepTeal,
                    2 to BrandOceanTeal,
                    1 to BrandMint,
                    2 to BrandCoral,
                    2 to BrandOceanTeal,
                    1 to BrandDeepTeal
                )
                stages.filter { it.second > 0 }.forEach { (_, minutes, color) ->
                    Box(
                        modifier = Modifier
                            .weight(minutes.toFloat())
                            .height(34.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(color)
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "11 PM", color = colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    "2 AM", color = colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    "5 AM", color = colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    "7 AM", color = colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                stages.forEach { (name,minutes, color) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(color)
                        )
                        Text(
                            text = "$name ${formatSleepDuration(minutes)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

}


@SuppressLint("DefaultLocale")
@Composable
private fun WeeklySleepChart(selectedPeriod:String,sleepRecords: List<SleepEntity>){
    val colorScheme = MaterialTheme.colorScheme
    val today = LocalDate.now()
    val (sleepHours, labels) = when (selectedPeriod) {

        "Week" -> {
            val dates = (0..6).map { today.minusDays((6 - it).toLong()) }

            val hours = dates.map { date ->
                val record = sleepRecords.firstOrNull {
                    it.date == date.toString()
                }

                (record?.totalSleepMinutes ?: 0) / 60f
            }

            val dayLabels = dates.map {
                it.dayOfWeek.getDisplayName(
                    java.time.format.TextStyle.SHORT,
                    java.util.Locale.getDefault()
                ).take(1)
            }

            hours to dayLabels
        }

        "Month" -> {
            val firstDay = today.withDayOfMonth(1)

            val weeklyHours = (0..3).map { weekIndex ->
                val weekStart = firstDay.plusDays((weekIndex * 7).toLong())
                val weekEnd = weekStart.plusDays(7)

                val records = sleepRecords.filter { record ->
                    val recordDate = java.time.LocalDate.parse(record.date)

                    !recordDate.isBefore(weekStart) &&
                            recordDate.isBefore(weekEnd) &&
                            recordDate.month == today.month &&
                            recordDate.year == today.year
                }

                if (records.isNotEmpty()) {
                    records.map { it.totalSleepMinutes }.average().toFloat() / 60f
                } else {
                    0f
                }
            }

            weeklyHours to listOf("W1", "W2", "W3", "W4")
        }

        "Year" -> {
            val monthlyHours = (1..12).map { month ->
                val records = sleepRecords.filter { record ->
                    val recordDate = java.time.LocalDate.parse(record.date)

                    recordDate.year == today.year &&
                            recordDate.monthValue == month
                }

                if (records.isNotEmpty()) {
                    records.map { it.totalSleepMinutes }.average().toFloat() / 60f
                } else {
                    0f
                }
            }

            val monthLabels = (1..12).map { month ->
                java.time.Month.of(month).getDisplayName(
                    java.time.format.TextStyle.SHORT,
                    java.util.Locale.getDefault()
                )
            }

            monthlyHours to monthLabels
        }

        else -> {
            val record = sleepRecords.firstOrNull {
                it.date == today.toString()
            }

            listOf((record?.totalSleepMinutes ?: 0) / 60f) to
                    listOf("Today")
        }
    }
    val recordedSleepHours = sleepHours.filter { it > 0f }


    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(3.dp)
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ){
            Column {
                Text(
                    text = when(selectedPeriod){
                        "Week" -> "Weekly Sleep"
                        "Month" -> "Monthly Sleep"
                        "Year" -> "Yearly Sleep"
                        else -> "Sleep Overview"
                    },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )

                Text(
                    text = when(selectedPeriod){
                        "Week" -> "Your sleep duration this week"
                        "Month" -> "Your sleep duration this month"
                        "Year" -> "Your sleep duration this year"
                        else -> "Your sleep duration"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.onSurfaceVariant
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(min = if (selectedPeriod == "Year") 560.dp else 280.dp)
                    .height(150.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ){
                sleepHours.forEachIndexed { index, hours ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ){
                        Text(
                            text = "${hours}h",
                            style = MaterialTheme.typography.labelSmall,
                            color = colorScheme.onSurfaceVariant
                        )
                        Box(
                            modifier = Modifier
                                .width(32.dp)
                                .height(if (hours > 0f) (hours / 9f * 90f).dp else 3.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    when {
                                        hours == 0f -> colorScheme.surfaceVariant
                                        index == sleepHours.indexOfLast { it > 0f } -> BrandDeepTeal
                                        else -> BrandMint
                                    }
                                )
                        )
                        Text(
                            text = labels[index],
                            style = MaterialTheme.typography.labelMedium,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Text(
                text = if (recordedSleepHours.isNotEmpty()) {
                    "Average: ${
                        String.format("%.1f", recordedSleepHours.average())
                    } hours per night"
                } else {
                    "No sleep records available"
                },
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = BrandOceanTeal
            )
        }
    }
}
@Composable
private fun SleepScoreRing(totalSleepMinutes: Int, goalMinutes: Int, sleepScore:Int){
    val timeProgress = if (goalMinutes > 0) {
        (totalSleepMinutes.toFloat() / goalMinutes).coerceIn(0f, 1f)
    } else {
        0f
    }

    val scoreProgress = (sleepScore.coerceIn(0, 100)) / 100f

    Box(
        modifier = Modifier.size(230.dp),
        contentAlignment = Alignment.Center
    ){
        Canvas(
            modifier = Modifier.fillMaxSize()
        ){
            val outerStroke = 14.dp.toPx()
            val innerStroke = 13.dp.toPx()
            // Outer track
            drawArc(
                color = BrandOceanTeal.copy(alpha = 0.12f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(12.dp.toPx(), 12.dp.toPx()),
                size = Size(
                    size.width - 24.dp.toPx(),
                    size.height - 24.dp.toPx()
                ),
                style = Stroke(width = outerStroke)
            )

            // Outer progress: time asleep
            drawArc(
                color = BrandOceanTeal,
                startAngle = -90f,
                sweepAngle = 360f * timeProgress,
                useCenter = false,
                topLeft = Offset(12.dp.toPx(), 12.dp.toPx()),
                size = Size(
                    size.width - 24.dp.toPx(),
                    size.height - 24.dp.toPx()
                ),
                style = Stroke(
                    width = outerStroke,
                    cap = StrokeCap.Round
                )
            )

            // Inner track
            drawArc(
                color = BrandCoral.copy(alpha = 0.12f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(36.dp.toPx(), 36.dp.toPx()),
                size = Size(
                    size.width - 72.dp.toPx(),
                    size.height - 72.dp.toPx()
                ),
                style = Stroke(width = innerStroke)
            )

            // Inner progress: sleep quality
            drawArc(
                color = BrandCoral,
                startAngle = -90f,
                sweepAngle = 360f * scoreProgress,
                useCenter = false,
                topLeft = Offset(36.dp.toPx(), 36.dp.toPx()),
                size = Size(
                    size.width - 72.dp.toPx(),
                    size.height - 72.dp.toPx()
                ),
                style = Stroke(
                    width = innerStroke,
                    cap = StrokeCap.Round
                )
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = sleepScore.toString(),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "Sleep score",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
@Composable
fun SleepGoalProgress(totalSleepMinutes:Int, goalMinutes:Int){
    val colorScheme = MaterialTheme.colorScheme

    val progress = if (goalMinutes > 0) {
        totalSleepMinutes.toFloat() / goalMinutes
    } else {
        0f
    }

    val progressPercentage = (progress * 100).toInt()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sleep Goal",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )

                Text(
                    text = "$progressPercentage%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = BrandOceanTeal
                )
            }

            LinearProgressIndicator(
                progress = { progress.coerceIn(0f,1f)},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(50)),
                color = BrandOceanTeal,
                trackColor = colorScheme.surfaceVariant
            )

            Text(
                text = "${formatSleepDuration(totalSleepMinutes)} of ${formatSleepDuration(goalMinutes)} goal completed",
                style = MaterialTheme.typography.bodySmall,
                color = colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SleepHistorySection(sleepRecords: List<SleepEntity>,onDelete:(Int)-> Unit, onUpdate:(SleepEntity) -> Unit,
                        onDeleteAll:()-> Unit, onEdit:(SleepEntity) -> Unit){
    val colorScheme = MaterialTheme.colorScheme
    var showDeleteAllDialog by remember {mutableStateOf(false)}
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Sleep History",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onBackground
            )

            if (sleepRecords.isNotEmpty()) {
                TextButton(
                    onClick = {
                        showDeleteAllDialog = true
                    }
                ) {
                    Text(
                        text = "Delete All",
                        color = BrandCoral
                    )
                }
            }
        }

        if (sleepRecords.isEmpty()) {
            Text(
                text = "No sleep records yet.",
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurfaceVariant
            )
        } else {
            sleepRecords.forEach { sleep ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(2.dp)
                ){
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ){
                        Text(
                            text = formatSleepDate(sleep.date),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onSurface
                        )

                        Text(
                            text = formatSleepDuration(sleep.totalSleepMinutes),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = BrandOceanTeal
                        )

                        Text(
                            text = "Score: ${sleep.sleepScore}  •  " +
                                    "${sleep.bedtime} - ${sleep.wakeUpTime}",
                            style = MaterialTheme.typography.bodySmall,
                            color = colorScheme.onSurfaceVariant
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            Text(
                                text = "Edit",
                                modifier = Modifier.clickable {
                                    onEdit(sleep)
                                },
                                color = BrandOceanTeal,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "Delete",
                                modifier = Modifier.clickable {
                                    onDelete(sleep.id)
                                },
                                color = BrandCoral,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        if (showDeleteAllDialog) {
            AlertDialog(
                onDismissRequest = {
                    showDeleteAllDialog = false
                },
                title = {
                    Text("Delete All Sleep Records?")
                },
                text = {
                    Text("Are you sure you want to delete all your sleep history? This action cannot be undone.")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onDeleteAll()
                            showDeleteAllDialog = false
                        }
                    ) {
                        Text(
                            "Delete All",
                            color = BrandCoral
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDeleteAllDialog = false
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }

    }
}

@Composable
fun TimePickerDialog(onCancel:()-> Unit, onConfirm:()-> Unit, content:@Composable () -> Unit){
    AlertDialog(
        onDismissRequest = onCancel,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Select")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text("Cancel")
            }
        },
        text = {
            content()
        }
    )
}

fun formatSleepDuration(minutes:Int):String{
    val hours = minutes / 60
    val remainingMinutes = minutes % 60

    return "${hours}h ${remainingMinutes.toString().padStart(2, '0')}m"
}

fun calculateSleepDuration(bedtime: String, wakeUpTime: String): Int? {
    return try {
        val bedMinutes = java.time.LocalTime.parse(bedtime)
            .toSecondOfDay() / 60

        val wakeMinutes = java.time.LocalTime.parse(wakeUpTime)
            .toSecondOfDay() / 60

        val duration = (wakeMinutes - bedMinutes + 1440) % 1440

        duration.takeIf { it > 0 }
    } catch (e: Exception) {
        null
    }
}
fun formatSleepDate(date: String): String {
    return try {
        java.time.LocalDate.parse(date).format(
            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
        )
    } catch (e: Exception) {
        date
    }
}