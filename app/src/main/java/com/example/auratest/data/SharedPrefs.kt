package com.example.auratest.data

import android.content.Context
import android.os.Bundle

class SharedPrefs(context: Context) {

    private val sharedPref = context.getSharedPreferences("", Context.MODE_PRIVATE)

    private val LAST_BOOT_DATA_KEY = "LAST_BOOT_DATA_KEY"
    private val LAST_BOOT_DATE = "LAST_BOOT_DATE"

    fun saveLastBootDate(timeInMillis: Long) {
        sharedPref
            .edit()
            .putLong(LAST_BOOT_DATE, timeInMillis)
            .apply()

    }

    fun getLastBootDate(): Long {
        val data = sharedPref.getLong(LAST_BOOT_DATE, 0L)
        return data
    }

    fun saveBootIntentData(action: String?, extras: Bundle?) {
        sharedPref
            .edit()
            .putStringSet(LAST_BOOT_DATA_KEY, setOf(action, extras.toString()))
            .apply()
    }

    fun getBootIntentData(): Set<String> {
        val data = sharedPref.getStringSet(LAST_BOOT_DATA_KEY, setOf()) ?: setOf()
        return data
    }


}