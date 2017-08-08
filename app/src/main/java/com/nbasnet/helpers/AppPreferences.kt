package com.nbasnet.helpers

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import kotlin.reflect.KClass

/**
 * Helper class for dealing with shared preferences
 */
class AppPreferences(context: Context) {
    private val preference: SharedPreferences = context.getSharedPreferences(
            AppPreferences::class.java.simpleName,
            Activity.MODE_PRIVATE
    )
    private val editor: SharedPreferences.Editor = preference.edit()

    fun <T : Comparable<T>> put(key: String, value: T): AppPreferences {
        when (value) {
            is String -> editor.putString(key, value).apply()
            is Int -> editor.putInt(key, value).apply()
            is Float -> editor.putFloat(key, value).apply()
            is Long -> editor.putLong(key, value).apply()
            is Boolean -> editor.putBoolean(key, value).apply()
            else -> throw Exception("$value is not supported for saving in shared preference in AppPreferences")
        }

        return this
    }

    fun <T> get(key: String, default: T): T {
        @Suppress("UNCHECKED_CAST")
        return when (default) {
            is String -> preference.getString(key, default) as T
            is Int -> preference.getInt(key, default) as T
            is Float -> preference.getFloat(key, default) as T
            is Long -> preference.getLong(key, default) as T
            is Boolean -> preference.getBoolean(key, default) as T
            else -> throw Exception("{T::class} is not supported for saving in shared preference in AppPreferences")
        }
    }
}