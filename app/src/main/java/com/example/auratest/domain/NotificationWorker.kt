package com.example.auratest.domain

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters


class NotificationWorker(val appContext: Context, val workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    val CHANNEL_ID = "AuraTestChannel"

    override fun doWork(): Result {

        val notificationManager = BootNotificationManager(appContext)
        notificationManager.showBootNotification()

        return Result.success()
    }


}