package com.akhilav.nutrilens.data.repository

import com.akhilav.nutrilens.data.local.dao.UserProfileDao
import com.akhilav.nutrilens.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileRepository @Inject constructor(
    private val userProfileDao: UserProfileDao
){
    fun getProfile(): Flow<UserProfileEntity?>{
        return userProfileDao.getProfile()
    }

    suspend fun saveProfile(profile: UserProfileEntity){
        userProfileDao.saveProfile(profile)
    }

    suspend fun deleteProfile(){
        userProfileDao.deleteProfile()
    }
}