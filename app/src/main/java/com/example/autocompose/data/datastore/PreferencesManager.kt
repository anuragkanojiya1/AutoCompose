package com.example.autocompose.data.datastore

import android.content.Context
import androidx.compose.ui.text.font.FontFamily
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreferencesManager(private val context: Context) {

    companion object {
        private val Context.dataStore by preferencesDataStore(name = "user_preferences")
        private val SUBSCRIPTION_KEY = stringPreferencesKey("subscription")
        private val HAS_SEEN_ONBOARDING_KEY = booleanPreferencesKey("has_seen_onboarding")
        private val FONT_FAMILY_KEY = stringPreferencesKey("font_family")
        private val LANGUAGE_KEY = stringPreferencesKey("language")
        private val WRITING_STYLE_KEY = stringPreferencesKey("writing_style")
    }

    suspend fun saveWritingStyle(writingStyle: String) {
        context.dataStore.edit { preferences ->
            preferences[WRITING_STYLE_KEY] = writingStyle
        }
    }

    val writingStyleFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[WRITING_STYLE_KEY] ?: "Formal"
        }

    suspend fun saveLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = language
        }
    }

    val languageFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[LANGUAGE_KEY] ?: "English"
        }

    suspend fun saveFontFamily(fontFamily: String) {
        context.dataStore.edit { preferences ->
            preferences[FONT_FAMILY_KEY] = fontFamily
        }
    }

    val fontFamilyFlow: Flow<FontFamily> = context.dataStore.data
        .map { preferences ->
            when (preferences[FONT_FAMILY_KEY]) {
                "Serif" -> FontFamily.Serif
                "SansSerif" -> FontFamily.SansSerif
                "Monospace" -> FontFamily.Monospace
                else -> FontFamily.Default
            }
        }

    // Save subscription tier
    suspend fun saveSubscriptionTier(tier: String) {
        context.dataStore.edit { preferences ->
            preferences[SUBSCRIPTION_KEY] = tier
        }
    }

    // Read subscription tier as Flow
    val subscriptionTierFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[SUBSCRIPTION_KEY] ?: "free"
        }

    // Save onboarding status
    suspend fun saveOnboardingStatus(hasSeenOnboarding: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[HAS_SEEN_ONBOARDING_KEY] = hasSeenOnboarding
        }
    }

    // Read onboarding status as Flow
    val hasSeenOnboardingFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[HAS_SEEN_ONBOARDING_KEY] ?: false
        }
}
