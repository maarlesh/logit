package com.chojikun.logit.core.util

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FirstLaunchManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val isFirstLaunchKey = booleanPreferencesKey("is_first_launch")

    val isFirstLaunch: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[isFirstLaunchKey] ?: true }

    suspend fun markLaunched() {
        context.dataStore.edit { prefs ->
            prefs[isFirstLaunchKey] = false
        }
    }
}
