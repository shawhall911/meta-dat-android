// SettingsManager - User Preferences for AI Service Selection
//
// Manages user preferences for AI service configuration

package com.meta.wearable.dat.externalsampleapps.smartview.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "smartview_settings")

enum class AiServiceType {
    HUGGING_FACE,
    MOCK
}

class SettingsManager(private val context: Context) {
    
    companion object {
        private val USE_MOCK_AI_KEY = booleanPreferencesKey("use_mock_ai")
    }
    
    val aiServiceType: Flow<AiServiceType> = context.dataStore.data
        .map { preferences ->
            val useMock = preferences[USE_MOCK_AI_KEY] ?: false
            if (useMock) AiServiceType.MOCK else AiServiceType.HUGGING_FACE
        }
    
    suspend fun setAiServiceType(type: AiServiceType) {
        context.dataStore.edit { preferences ->
            preferences[USE_MOCK_AI_KEY] = (type == AiServiceType.MOCK)
        }
    }
}
