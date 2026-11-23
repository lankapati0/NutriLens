package com.akhilav.nutrilens.ui.screens.weightTrack

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Fill
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.akhilav.nutrilens.ui.component.GlassCard
import com.akhilav.nutrilens.ui.theme.BrandMint
import com.akhilav.nutrilens.viewmodels.weightEntry.WeightViewModel
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.columnModel
import com.patrykandpatrick.vico.compose.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun WeightTrackingScreen(
    viewModel: WeightViewModel = hiltViewModel()
){

    val weightHistory by viewModel.weightHistory.collectAsStateWithLifecycle()
    val latestWeight by viewModel.latestWeight.collectAsStateWithLifecycle()
    var weightInput by remember { mutableStateOf("") }

    val weightChartModelProducer = remember { CartesianChartModelProducer() }
    val weightChartValues = weightHistory.sortedBy { it.recordedAt }.map{it.weightKg}

    val weightChartLabels = weightHistory.sortedBy { it.recordedAt }.map {
        SimpleDateFormat(
            "dd MMM",
            Locale.getDefault()
        ).format(Date(it.recordedAt))
    }

    LaunchedEffect(weightChartValues) {
        if (weightChartValues.isNotEmpty()) {
            weightChartModelProducer.runTransaction {
                columnModel {
                    series(weightChartValues)
                }
            }
        }
    }


        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Weight Progress",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Latest Weight",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(style = MaterialTheme.typography.headlineMedium,
                    text = latestWeight?.let {"${it.weightKg} kg"} ?: "No weight recorded yet")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

       if(weightChartValues.isNotEmpty()){
           GlassCard(
               modifier = Modifier.fillMaxWidth()
           ) {
               Column(
                   modifier = Modifier.padding(16.dp)
               ) {
                   Text(
                       text = "Weight Progress",
                       style = MaterialTheme.typography.titleMedium
                   )

                   Spacer(modifier = Modifier.height(12.dp))

                   CartesianChartHost(
                       chart = rememberCartesianChart(
                           rememberColumnCartesianLayer(
                               columnProvider = ColumnCartesianLayer.ColumnProvider.series(
                                   rememberLineComponent(fill = com.patrykandpatrick.vico.compose.common.Fill(BrandMint), thickness = 18.dp, shape = RoundedCornerShape(
                                           topStart = 8.dp,
                                           topEnd = 8.dp
                                       ))
                               )
                           ),
                           startAxis = VerticalAxis.rememberStart(),
                           bottomAxis = HorizontalAxis.rememberBottom(
                               valueFormatter = { _, x, _ ->
                                   weightChartLabels.getOrNull(x.toInt())
                                       ?: x.toInt().toString()
                               },
                               itemPlacer = remember {
                                   HorizontalAxis.ItemPlacer.aligned(
                                       spacing = { 1 }
                                   )
                               }
                           )
                       ),
                       modelProducer = weightChartModelProducer,
                       modifier = Modifier
                           .fillMaxWidth()
                           .height(240.dp)
                   )
               }
           }
       }


        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp)
            ){
                Text(
                    text = "Record Weight",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = weightInput,
                    onValueChange = { weightInput = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Weight (kg)") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    singleLine = true
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
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Weight")
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Weight History",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(12.dp))
        if(weightHistory.isEmpty()){
            Text(
                text = "No weight history available.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(weightHistory){entry ->
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Text(
                                text = SimpleDateFormat(
                                    "dd MMM yyyy, hh:mm a",
                                    Locale.getDefault()
                                ).format(Date(entry.recordedAt))
                            )

                            Text(
                                text = "${entry.weightKg} kg",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }

    }

}