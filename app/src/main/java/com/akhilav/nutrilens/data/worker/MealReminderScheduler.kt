package com.akhilav.nutrilens.data.worker

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

object MealReminderScheduler {
    private const val WORK_NAME = "daily_meal_reminder"

    fun schedule(context: Context,hour:Int, minute:Int){
        val now = Calendar.getInstance()
        val reminderTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY,hour)
            set(Calendar.MINUTE,minute)
            set(Calendar.SECOND,0)
            set(Calendar.MILLISECOND,0)

            if(timeInMillis <= now.timeInMillis){
                add(Calendar.DAY_OF_YEAR,1)
            }
        }
        val inputData = Data.Builder().putInt("hour",hour)
            .put("minute",minute).build()

        val initialDelay = reminderTime.timeInMillis - now.timeInMillis


        val workRequest = OneTimeWorkRequestBuilder<MealReminderWorker>()
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(WORK_NAME,
            ExistingWorkPolicy.REPLACE,workRequest)
    }

    fun scheduleNext(context: Context,hour: Int,minute: Int){
        val now = Calendar.getInstance()
        val reminderTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if(timeInMillis <= now.timeInMillis){
                add(Calendar.DAY_OF_YEAR,1)
            }
        }

        val request = OneTimeWorkRequestBuilder<MealReminderWorker>()
            .setInitialDelay(reminderTime.timeInMillis - now.timeInMillis, TimeUnit.MILLISECONDS)
            .setInputData(Data.Builder().putInt("hour",hour).putInt("minute",minute).build()).build()

        WorkManager.getInstance(context).enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.APPEND_OR_REPLACE,request)

    }

    fun cancel(context: Context){
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }

}