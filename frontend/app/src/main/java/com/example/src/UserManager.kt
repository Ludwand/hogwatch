package com.example.src

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID

//create file called user_settings in internal storage
private val Context.dataStore by preferencesDataStore(name = "user_Settings")

//private constructor = singleton
class UserManager private constructor(context: Context) {
    //context is like a "handle" needed to open local device storage
    // Store only the DataStore instance, NOT the Context itself
    private val dataStore = context.applicationContext.dataStore
    private val USER_ID_KEY = stringPreferencesKey("app_user_id") //enforce app_user_id as string

    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch { //Avoids running IO-blocking on main thread
            val id = getOrCreateUserId()
            _userId.value = id
        }
    }

    private suspend fun getOrCreateUserId(): String {
        val existingId = dataStore.data
            .map { prefs -> prefs[USER_ID_KEY] }
            .first()

        if (existingId != null) return existingId

        val newId = UUID.randomUUID().toString()
        dataStore.edit { prefs ->
            prefs[USER_ID_KEY] = newId
        }
        return newId
    }

    /*
    * companion object contains functions and properties tied to the class itself rather than a specific instance
    */
    companion object {
        @Volatile
        private var INSTANCE: UserManager? = null

        fun getInstance(context: Context): UserManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}