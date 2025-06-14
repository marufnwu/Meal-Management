package com.logicline.mydining.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.logicline.mydining.BuildConfig
import com.logicline.mydining.data.models.MessUser
import com.logicline.mydining.data.models.Month
import com.logicline.mydining.data.models.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/**
 * Singleton object for managing application preferences with SharedPreferences
 * Supports both synchronous and reactive access patterns
 */
object AppPrefs {

    internal const val ACCESS_TOKEN = "access_token"
    internal const val USER = "user"
    private const val MONTH = "month"
    internal const val MESS_USER = "mess_user"
    internal const val USER_ID = "user_id"
    private const val MONTH_ID = "month_id"
    private const val TAG = "AppPrefs"
    private const val PREF_NAME = "MealManagement"

    private lateinit var appContext: Context
    val gson by lazy { Gson() }

    // Declare as public to allow safe inline access
    @PublishedApi
    internal val isDebugMode = BuildConfig.DEBUG

    val prefs: SharedPreferences by lazy {
        if (!::appContext.isInitialized) {
            throw IllegalStateException("AppPrefs must be initialized with init() before use")
        }
        appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    // LiveData for preference changes
    private val _preferenceChanges = MutableLiveData<String>()
    val preferenceChanges: LiveData<String> = _preferenceChanges

    // Changed from nullable to non-nullable with lateinit
    private lateinit var prefChangeListener: SharedPreferences.OnSharedPreferenceChangeListener

    /**
     * Initialize AppPrefs with application context
     * Must be called before using any preference methods
     */
    fun init(context: Context) {
        appContext = context.applicationContext

        // Create the listener with thread-safe value posting
        prefChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            // Use setValue for main thread or postValue properly for background threads
            try {
                // Handle the preference change on the main thread
                android.os.Handler(android.os.Looper.getMainLooper()).post {
                    key?.let { nonNullKey ->
                        _preferenceChanges.value = nonNullKey
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error posting preference change: ${e.message}")
            }
        }

        // Explicitly access the prefs property to ensure it's initialized
        // before registering the listener
        val sharedPrefs = prefs
        sharedPrefs.registerOnSharedPreferenceChangeListener(prefChangeListener)
    }

    /**
     * Clean up resources when no longer needed
     */
    fun cleanup() {
        if (::prefChangeListener.isInitialized && ::appContext.isInitialized) {
            prefs.unregisterOnSharedPreferenceChangeListener(prefChangeListener)
        }
    }

    /**
     * Clears all preferences
     */
    fun clearAll() {
        prefs.edit { clear() }
    }

    /**
     * Helper for safe logging in inline functions
     */
    @PublishedApi
    internal fun logDebug(message: String) {
        if (isDebugMode) {
            Log.d(TAG, message)
        }
    }

    @PublishedApi
    internal fun logError(message: String, e: Exception? = null) {
        Log.e(TAG, message, e)
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

    /**
     * Store an object as JSON in SharedPreferences
     * @param key The preference key
     * @param obj The object to store
     */
    inline fun <reified T> putObject(key: String, obj: T) {
        val json = gson.toJson(obj)
        logDebug("putObject: KEY= $key")
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

    /**
     * Retrieve an object stored as JSON from SharedPreferences
     * @param key The preference key
     * @param default Default value if preference doesn't exist
     * @return The deserialized object or default if not found/invalid
     */
    inline fun <reified T> getObject(key: String, default: T? = null): T? {
        val json = prefs.getString(key, null) ?: return default
        logDebug("getObject: KEY= $key")
        return try {
            gson.fromJson(json, object : TypeToken<T>() {}.type)
        } catch (e: Exception) {
            logError("Error deserializing object for key '$key': ${e.message}", e)
            default
        }
    }

    fun remove(key: String) {
        prefs.edit { remove(key) }
    }

    fun contains(key: String): Boolean = prefs.contains(key)

    // --- Key Dedicated Setters/Getters ---
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

    var month: Month?
        get() = getObject(MONTH)
        set(value) {
            if (value != null) {
                putInt(MONTH_ID, value.id)
                putObject(MONTH, value)
            }
            else {
                remove(MONTH)
                remove(MONTH_ID)
            }
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

    /**
     * Observe an object stored as JSON in SharedPreferences as a Flow
     * @param key The preference key
     * @param defaultValue Default value if preference doesn't exist
     * @return Flow of the deserialized object
     */
    inline fun <reified T> getObjectFlow(key: String, defaultValue: T? = null): Flow<T?> =
        getStringFlow(key, null).map { jsonString ->
            if (jsonString == null) {
                defaultValue
            } else {
                try {
                    val deserializedObject: Any? = gson.fromJson(jsonString, object : TypeToken<T>() {}.type)

                    if (deserializedObject is T || deserializedObject == null) {
                        deserializedObject // Smart cast is sufficient
                    } else {
                        // deserializedObject is not null and not an instance of T.
                        // It's often a LinkedTreeMap if Gson couldn't directly map to T.
                        logDebug(
                            "Initial deserialization for key '$key' resulted in ${deserializedObject.javaClass.name}, not ${T::class.java.name}. JSON: $jsonString"
                        )

                        // Attempt to convert if it's a map-like structure.
                        if (deserializedObject is Map<*, *>) {
                            logDebug("Attempting to convert Map to ${T::class.java.name} for key '$key'")
                            try {
                                // Convert the Map object back to a JSON string
                                val intermediateJson = gson.toJson(deserializedObject)
                                // Then parse this JSON string into the specific type T
                                val finalObject: T? = gson.fromJson(intermediateJson, object : TypeToken<T>() {}.type)
                                finalObject
                            } catch (e: Exception) {
                                logError(
                                    "Failed to convert Map to ${T::class.java.name} for key '$key' during secondary attempt: ${e.message}. Original JSON: $jsonString",
                                    e
                                )
                                defaultValue // Fallback to defaultValue if secondary conversion fails
                            }
                        } else {
                            // If it's not a Map, and not T, we can't do much more.
                            logError(
                                "Type mismatch for key '$key': Expected ${T::class.java.name} or Map, but got ${deserializedObject.javaClass.name}. Cannot convert. JSON: $jsonString",
                                null
                            )
                            defaultValue
                        }
                    }
                } catch (e: com.google.gson.JsonParseException) {
                    logError("Error parsing JSON in getObjectFlow for key '$key': ${e.message}. JSON: $jsonString", e)
                    defaultValue
                } catch (e: Exception) {
                    logError("Unexpected error in getObjectFlow for key '$key': ${e.message}. JSON: $jsonString", e)
                    defaultValue
                }
            }
        }

    inline fun <reified T> getObjectFlowStrict(key: String, defaultValue: T? = null): Flow<T?> {
        val type = T::class.java
        return getStringFlow(key, null).map { json ->
            if (json == null) return@map defaultValue
            try {
                gson.fromJson(json, type)
            } catch (e: Exception) {
                logError("Strict JSON deserialization failed for $key: ${e.message}", e)
                defaultValue
            }
        }
    }

    // Reactive versions of dedicated properties
    val accessTokenFlow: Flow<String?> = getStringFlow(ACCESS_TOKEN)
    val userFlow: Flow<User?> = getObjectFlow(USER)
    val messUserFlow: Flow<MessUser?> = getObjectFlow(MESS_USER)
    val userIdFlow: Flow<Int?> = getIntFlow(USER_ID).map { if (it == 0 && !contains(USER_ID)) null else it }
    val monthIdFlow: Flow<Int?> = getIntFlow(MONTH_ID).map { if (it == 0 && !contains(MONTH_ID)) null else it }
    val monthFlow: Flow<Month?> = getObjectFlowStrict(MONTH)
}