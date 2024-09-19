package com.example.auratest.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.auratest.data.SharedPrefs
import com.example.auratest.databinding.ActivityMainBinding
import com.example.auratest.domain.BootNotificationManager
import com.example.auratest.domain.NotificationWorker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    val requestNotificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                //granted for notifications. Do nothing
                binding.tvPermission.visibility = View.GONE
                binding.btnPermissions.visibility = View.GONE
            } else {
                // show text that the app needs permission
                binding.tvPermission.visibility = View.VISIBLE
                binding.btnPermissions.visibility = View.VISIBLE
            }
        }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnPermissions.setOnClickListener {
            checkNotificationPermission()
        }
        binding.tv.text = getDataFromDb(this)

//        enableEdgeToEdge()
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        checkNotificationPermission()
        // start the notification worker if it not started
        setNotificationWorker(this)

        val notificationManager = BootNotificationManager(this)
        notificationManager.showBootNotification()

    }

    private fun checkNotificationPermission() {

        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED -> {
                requestNotificationPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }

            else -> {
                // do nothing
            }
        }
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
        Log.d("NotificationWorker", "NotificationWorker was set from activity")
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
}