package com.akhilav.nutrilens.data.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.akhilav.nutrilens.utils.NotificationHelper

class MealReminderWorker(context: Context, workerParams: WorkerParameters): Worker(context,workerParams){

    override fun doWork(): Result {
        NotificationHelper.showNotification(context= applicationContext,title = "NutriLens Meal Reminder",
            message = "It's time to log your meal!" )
        val hour = inputData.getInt("hour",12)
        val minute = inputData.getInt("minute",0)

        MealReminderScheduler.scheduleNext(context = applicationContext,hour,minute)
        return Result.success()
    }
}