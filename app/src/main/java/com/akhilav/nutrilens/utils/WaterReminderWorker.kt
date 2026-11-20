package com.akhilav.nutrilens.utils

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class WaterReminderWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context,workerParams){
    override fun doWork(): Result {
        NotificationHelper.showNotification(applicationContext,
            title = "NutriLens Water Reminder",
            message = "Time to drink some water!")

        val hour = inputData.getInt("hour",10)
        val minute = inputData.getInt("minute",0)

        WaterReminderScheduler.scheduleNext(applicationContext,hour,minute)
        return Result.success()
    }
}