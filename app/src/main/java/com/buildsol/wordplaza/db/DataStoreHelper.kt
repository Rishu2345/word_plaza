package com.timestampcamera.intalyx.db

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreHelper(
    private val dataStore: DataStore<Preferences>
) {

    suspend fun putString(key: String, value: String) {
        dataStore.edit { it[stringPreferencesKey(key)] = value }
    }

    suspend fun putInt(key: String, value: Int) {
        dataStore.edit { it[intPreferencesKey(key)] = value }
    }

    suspend fun putBoolean(key: String, value: Boolean) {
        dataStore.edit { it[booleanPreferencesKey(key)] = value }
    }

    fun getString(key: String, default: String = ""): Flow<String> =
        dataStore.data.map { it[stringPreferencesKey(key)] ?: default }

    fun getInt(key: String, default: Int = 0): Flow<Int> =
        dataStore.data.map { it[intPreferencesKey(key)] ?: default }

    fun getBoolean(key: String, default: Boolean = false): Flow<Boolean> =
        dataStore.data.map { it[booleanPreferencesKey(key)] ?: default }

    suspend fun clear() {
        dataStore.edit { it.clear() }
    }
}
