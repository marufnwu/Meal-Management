package com.logicline.mydining.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.logicline.mydining.data.models.MessUser
import com.logicline.mydining.data.models.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlin.math.log

object AppPrefs {

    internal const val ACCESS_TOKEN = "access_token"
    internal const val USER = "user"
    internal const val MESS_USER = "mess_user"
    internal const val USER_ID = "user_id"
    internal const val MONTH_ID = "month_id"

    private const val PREF_NAME = "MealManagement"
    lateinit var appContext: Context

    val gson = Gson()

    val prefs: SharedPreferences by lazy {
        appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    // LiveData for preference changes
    private val _preferenceChanges = MutableLiveData<String>()
    val preferenceChanges: LiveData<String> = _preferenceChanges

    fun init(context: Context) {
        appContext = context.applicationContext
        // Register preference change listener
        prefs.registerOnSharedPreferenceChangeListener { _, key ->
            _preferenceChanges.postValue(key)
        }
    }

    fun clearAll() {
        prefs.edit { clear() }
    }

    // ---------- Generic Setters ----------
    fun putString(key: String, value: String?) {
        prefs.edit { putString(key, value) }
    }

    fun putInt(key: String, value: Int) {
        prefs.edit { putInt(key, value) }
    }

    fun putBoolean(key: String, value: Boolean) {
        prefs.edit { putBoolean(key, value) }
    }

    fun putLong(key: String, value: Long) {
        prefs.edit { putLong(key, value) }
    }

    fun putFloat(key: String, value: Float) {
        prefs.edit { putFloat(key, value) }
    }

    inline fun <reified T> putObject(key: String, obj: T) {
        val json = gson.toJson(obj)
        Log.d("AppPref", "putObject: KEY= $key DATA= $json")
        prefs.edit { putString(key, json) }
    }

    // ---------- Generic Getters ----------
    fun getString(key: String, default: String? = null): String? =
        prefs.getString(key, default)

    fun getInt(key: String, default: Int = 0): Int =
        prefs.getInt(key, default)

    fun getBoolean(key: String, default: Boolean = false): Boolean =
        prefs.getBoolean(key, default)

    fun getLong(key: String, default: Long = 0L): Long =
        prefs.getLong(key, default)

    fun getFloat(key: String, default: Float = 0f): Float =
        prefs.getFloat(key, default)

    inline fun <reified T> getObject(key: String, default: T? = null): T? {
        val json = prefs.getString(key, null) ?: return default
        Log.d("AppPref", "getObject: KEY= $key DATA= $json")
        return try {
            gson.fromJson(json, object : TypeToken<T>() {}.type)
        } catch (e: Exception) {
            Log.e("AppPref", "getObject: ${e.message}")
            null
        }
    }

    fun remove(key: String) {
        prefs.edit { remove(key) }
    }

    fun contains(key: String): Boolean = prefs.contains(key)

    // --- 🔑 Dedicated Setters/Getters ---
    var accessToken: String?
        get() = getString(ACCESS_TOKEN)
        set(value) = putString(ACCESS_TOKEN, value)

    var user: User?
        get() = getObject(USER)
        set(value) = putObject(USER, value)

    var messUser: MessUser?
        get() = getObject(MESS_USER)
        set(value) = putObject(MESS_USER, value)

    var userId: Int?
        get() = if (prefs.contains(USER_ID)) getInt(USER_ID) else null
        set(value) {
            if (value != null) putInt(USER_ID, value)
            else prefs.edit { remove(USER_ID) }
        }

    var monthId: Int?
        get() = if (prefs.contains(MONTH_ID)) getInt(MONTH_ID) else null
        set(value) {
            if (value != null) putInt(MONTH_ID, value)
            else prefs.edit { remove(MONTH_ID) }
        }

    // ---------- Reactive Extensions ----------

    /**
     * Observe changes to a specific preference key as Flow
     */
    internal inline fun <T> getPreferenceFlow(
        key: String,
        defaultValue: T,
        crossinline getter: SharedPreferences.(String, T) -> T
    ): Flow<T> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, changedKey ->
            if (changedKey == key) {
                trySend(prefs.getter(key, defaultValue))
            }
        }

        // Send initial value
        send(prefs.getter(key, defaultValue))

        // Register listener
        prefs.registerOnSharedPreferenceChangeListener(listener)

        // Unregister when flow collection ends
        awaitClose {
            prefs.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }.distinctUntilChanged()

    // Convenience methods for common types
    fun getStringFlow(key: String, defaultValue: String? = null): Flow<String?> =
        getPreferenceFlow(key, defaultValue, SharedPreferences::getString)

    fun getIntFlow(key: String, defaultValue: Int = 0): Flow<Int> =
        getPreferenceFlow(key, defaultValue, SharedPreferences::getInt)

    fun getBooleanFlow(key: String, defaultValue: Boolean = false): Flow<Boolean> =
        getPreferenceFlow(key, defaultValue, SharedPreferences::getBoolean)

    fun getLongFlow(key: String, defaultValue: Long = 0L): Flow<Long> =
        getPreferenceFlow(key, defaultValue, SharedPreferences::getLong)

    inline fun <reified T> getObjectFlow(key: String, defaultValue: T? = null): Flow<T?> =
        getStringFlow(key, null).map { json ->
            json?.let {
                try {
                    gson.fromJson(it, object : TypeToken<T>() {}.type)
                } catch (e: Exception) {
                    Log.e("AppPref", "getObjectFlow: ${e.message}")
                    defaultValue
                }
            } ?: defaultValue
        }

    // Reactive versions of dedicated properties
    val accessTokenFlow: Flow<String?> = getStringFlow(ACCESS_TOKEN)
    val userFlow: Flow<User?> = getObjectFlow(USER)
    val messUserFlow: Flow<MessUser?> = getObjectFlow(MESS_USER)
    val userIdFlow: Flow<Int?> = getIntFlow(USER_ID).map { if (it == 0) null else it }
    val monthIdFlow: Flow<Int?> = getIntFlow(MONTH_ID).map { if (it == 0) null else it }
}