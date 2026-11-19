package com.akhilav.nutrilens.viewmodels.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akhilav.nutrilens.data.local.entity.ScanHistoryEntity
import com.akhilav.nutrilens.data.repository.ScanHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScanHistoryViewModel @Inject constructor(
    private val repository: ScanHistoryRepository
): ViewModel(){
    val scanHistory: StateFlow<List<ScanHistoryEntity>> =
                    repository.getAllScans().stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000), initialValue = emptyList())

    fun deleteScan(id:Int){
        viewModelScope.launch {
            repository.deleteScan(id)
        }
    }
    fun clearHistory(){
        viewModelScope.launch {
            repository.clearHistory()
        }
    }
}