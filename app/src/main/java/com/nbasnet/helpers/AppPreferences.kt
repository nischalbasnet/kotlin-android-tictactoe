package com.nbasnet.helpers

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

/**
 * Helper class for dealing with shared preferences
 */
class AppPreferences(context: Context) {
    val preference: SharedPreferences = context.getSharedPreferences(
            AppPreferences::class.java.simpleName,
            Activity.MODE_PRIVATE
    )
    val editor: SharedPreferences.Editor = preference.edit()

    fun <T : Comparable<T>> put(key: String, value: T): AppPreferences {
        when (value) {
            is String -> editor.putString(key, value).apply()
            is Int -> editor.putInt(key, value).apply()
            is Float -> editor.putFloat(key, value).apply()
            is Long -> editor.putLong(key, value).apply()
            is Boolean -> editor.putBoolean(key, value).apply()
            else -> Exception("$value is not supported for saving in shared preference in AppPreferences")
        }

        return this
    }
}