package com.akhilav.nutrilens.viewmodels.fasting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akhilav.nutrilens.data.local.entity.FastingSessionEntity
import com.akhilav.nutrilens.data.repository.FastingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FastingTrackerViewModel @Inject constructor(
    private val repository: FastingRepository
): ViewModel(){
    val activeSession = repository.getActiveSession()
    val allSessions = repository.getAllSessions()

    fun startFast(targetHours:Int){
        viewModelScope.launch {
            repository.startFast(targetHours)
        }
    }

    fun endFast(session: FastingSessionEntity){
        viewModelScope.launch {
            repository.endFast(session)
        }
    }
}