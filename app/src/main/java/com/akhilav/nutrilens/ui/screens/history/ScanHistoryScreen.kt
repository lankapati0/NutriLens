package com.akhilav.nutrilens.ui.screens.history

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.akhilav.nutrilens.viewmodels.history.ScanHistoryViewModel

@Composable
fun ScanHistoryScreen(
    viewModel: ScanHistoryViewModel = hiltViewModel()
){
    val scanHistory by viewModel.scanHistory.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ){
        Text(
            text = "Scan History",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))
       if(scanHistory.isEmpty()){
           Text(
               text = "No scanned products yet.",
               style = MaterialTheme.typography.bodyMedium
           )
       } else {
           LazyColumn {
               items(scanHistory, key = {it.id}){scan ->
                   Card(modifier = Modifier.fillMaxWidth()
                       .padding(vertical = 4.dp)) {
                       Row( modifier = Modifier
                           .fillMaxWidth()
                           .padding(16.dp),
                           verticalAlignment = Alignment.CenterVertically) {
                           Column(
                               modifier = Modifier.weight(1f)
                           ){
                               Text(
                                   text=scan.productName ?: "Unknown product",
                                   style = MaterialTheme.typography.titleMedium
                               )
                               Text(
                                   style = MaterialTheme.typography.bodySmall,
                                   text = "Barcode: ${scan.barcode}"
                               )
                           }

                           IconButton(
                               onClick = {
                                   viewModel.deleteScan(scan.id)
                               }
                           ){
                               Icon(
                                   imageVector = Icons.Default.Delete,
                                   contentDescription = "Delete scan"
                               )
                           }
                       }
                   }
               }
           }
       }
    }
}