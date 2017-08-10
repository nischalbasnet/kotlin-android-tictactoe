package com.nbasnet.helpers

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Helper class for dealing with shared preferences
 */
class AppPreferences(context: Context) : BaseAppPreference() {
    override val appPreferenceHandler = AppPreferencesHandler(context)

    var selectedLanguagePosition: Int by delegateProp(0)
    var selectedLanguage: String by delegateProp("english")
    var userAge: Int by delegateProp(0)
    var player1Name: String by delegateProp("")
    var player2Name: String by delegateProp("")
    var isPlayer1AI: Boolean by delegateProp(false)
    var isPlayer2AI: Boolean by delegateProp(false)
    var startPlayer: Int by delegateProp(1)
    var player1WinsCount: Int by delegateProp(0)
    var player2WinsCount: Int by delegateProp(0)
}

abstract class BaseAppPreference {
    abstract protected val appPreferenceHandler: AppPreferencesHandler

    protected fun <T> delegateProp(default: T, key: String? = null): PreferenceDelegate<AppPreferences, T> {
        return PreferenceDelegate(appPreferenceHandler, default, key)
    }
}

/**
 * Delegate class for AppPreferences class property
 */
class PreferenceDelegate<in R, T>(
        val appPreferenceHandler: AppPreferencesHandler,
        val default: T,
        val key: String? = null
) : ReadWriteProperty<R, T> {
    override fun getValue(thisRef: R, property: KProperty<*>): T {
        val propKey = key ?: property.name
        return appPreferenceHandler.get(propKey, default)
    }

    override fun setValue(thisRef: R, property: KProperty<*>, value: T) {
        val propKey = key ?: property.name
        appPreferenceHandler.put(propKey, value)
    }
}

/**
 * Class that handles setting and retrieving values from shared preference
 */
class AppPreferencesHandler(context: Context) {
    private val preference: SharedPreferences = context.getSharedPreferences(
            AppPreferencesHandler::class.java.simpleName,
            Activity.MODE_PRIVATE
    )
    private val editor: SharedPreferences.Editor = preference.edit()

    fun <T> put(key: String, value: T): AppPreferencesHandler {
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