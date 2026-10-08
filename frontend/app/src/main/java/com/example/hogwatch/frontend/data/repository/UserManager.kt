package com.example.hogwatch.frontend.data.repository

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
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


   val userIdFlow: Flow<String?> = dataStore.data.map { preferences ->
      preferences[USER_ID_KEY]
   }

   suspend fun getUserId(): String {
      val existingId = dataStore.data.map { it[USER_ID_KEY] }.first()
      if (existingId != null) {
         return existingId
      }

      val newId = UUID.randomUUID().toString()
      dataStore.edit { preferences ->
         preferences[USER_ID_KEY] = newId
      }
      return newId
   }

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