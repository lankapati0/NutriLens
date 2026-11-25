package com.akhilav.nutrilens.viewmodels.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akhilav.nutrilens.data.local.entity.UserProfileEntity
import com.akhilav.nutrilens.data.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val repository: UserProfileRepository
): ViewModel(){

    val profile = repository.getProfile().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000),null)
    val isProfileLoaded = repository.getProfile()
        .map { true }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            false
        )
    fun saveProfile(profile: UserProfileEntity){
        viewModelScope.launch {
            repository.saveProfile(profile)
        }
    }
}