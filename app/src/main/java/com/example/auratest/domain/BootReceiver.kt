package com.example.auratest.domain

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.auratest.data.SharedPrefs
import java.util.concurrent.TimeUnit

class BootReceiver : BroadcastReceiver() {

    private val log_tag = "BootReceiver"

    override fun onReceive(context: Context?, intent: Intent?) {

        val action = intent?.action
        val extras = intent?.extras

        Log.d(log_tag, action.toString())
        Log.d(log_tag, extras.toString())

        context?.let {
            // todo check nullable context
            saveDataToDb(context, action, extras)
            setNotificationWorker(context)

            //show notification now
            val notificationManager = BootNotificationManager(context)
            notificationManager.showBootNotification()
        }

    }

    private fun saveDataToDb(context: Context, action: String?, extras: Bundle?) {

        // todo change for database, for example Room
        val prefs = SharedPrefs(context)
        prefs.saveBootIntentData(action, extras)
        prefs.saveLastBootDate(System.currentTimeMillis())
    }

    private fun setNotificationWorker(context: Context) {
        val notificationWorker: PeriodicWorkRequest =
            PeriodicWorkRequestBuilder<NotificationWorker>(15, TimeUnit.MINUTES)
                .build()
        WorkManager
            .getInstance(context)
            // sets the worker if it was not set before
            .enqueueUniquePeriodicWork(
                NotificationWorker::class.simpleName
                    ?: "NotificationWorker", // todo change the name
                ExistingPeriodicWorkPolicy.KEEP, notificationWorker
            )
        Log.d("NotificationWorker", "NotificationWorker was set from BootReceiver")

    }


}