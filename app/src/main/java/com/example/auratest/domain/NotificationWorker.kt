package com.example.auratest.domain

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.auratest.R
import com.example.auratest.data.SharedPrefs
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class NotificationWorker(val appContext: Context, val workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    val CHANNEL_ID = "AuraTestChannel"

    override fun doWork(): Result {

        val textForNotification = getDataFromDb(appContext)

        createNotificationChannel(appContext)
        createNotification(appContext, textForNotification)

        return Result.success()
    }

    private fun getDataFromDb(context: Context): String {
        // todo get data from Shared prefs for now. Could be changed to database
        val prefs = SharedPrefs(context)
        val data = prefs.getBootIntentData()

        val lastBootDate = prefs.getLastBootDate()
        if (lastBootDate > 0) {
            val date = Date(lastBootDate)
            val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
            val formattedDate = formatter.format(date)
            val contentForShown = "The boot was detected at: $formattedDate"
            return contentForShown
        } else {
            return "No boots detected"

        }
    }

    private fun createNotification(context: Context, textForNotification: String) {

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_airplane_24)
            .setContentTitle("Aura title")
            .setContentText(textForNotification)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .build()

        //for android api > 33
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) && (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
                    )
        ) {
            // do nothing
            return
        }
        val id = System.currentTimeMillis().toInt()
        NotificationManagerCompat.from(context).notify(id, notification)

    }

    private fun createNotificationChannel(context: Context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.channel_name)
            val descriptionText = context.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH // cannot set max
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system.
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}