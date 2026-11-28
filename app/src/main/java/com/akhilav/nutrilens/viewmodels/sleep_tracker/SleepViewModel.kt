package com.akhilav.nutrilens.viewmodels.sleep_tracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akhilav.nutrilens.data.local.entity.SleepEntity
import com.akhilav.nutrilens.data.repository.SleepRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class SleepViewModel @Inject constructor(
    private val sleepRepository: SleepRepository
): ViewModel(){
    val sleepRecords: StateFlow<List<SleepEntity>> =
        sleepRepository.getAllSleepRecords().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )
    fun addSleepRecord(sleep: SleepEntity){
        viewModelScope.launch {
            sleepRepository.insertSleepRecord(sleep)
        }
    }
    fun updateSleepRecord(sleep: SleepEntity){
        viewModelScope.launch {
            sleepRepository.updateSleepRecord(sleep)
        }
    }

    fun deleteSleepRecord(sleepId:Int){
        viewModelScope.launch {
            sleepRepository.deleteSleepRecord(sleepId)
        }
    }

    fun deleteAllSleepRecord(){
        viewModelScope.launch { sleepRepository.deleteAllSleepRecords() }
    }

    suspend fun getSleepRecordByDate(date:String): SleepEntity? {
        return sleepRepository.getSleepRecordByDate(date)
    }
}