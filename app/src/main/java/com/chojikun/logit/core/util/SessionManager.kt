package com.chojikun.logit.core.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

@Singleton
class SessionManager @Inject constructor(
    @param:ApplicationContext private val context : Context
)  {
    private val accessToken = stringPreferencesKey("access_token")
    private val refreshToken = stringPreferencesKey("refresh_token")
    private val kdfSalt = stringPreferencesKey("kdf_salt")
    private val kdfParams = stringPreferencesKey("kdf_params")
    private val wrappedVaultKey = stringPreferencesKey("wrapped_vault_key")
    private val vaultKeyNonce = stringPreferencesKey("vault_key_nonce")

    val isLoggedIn: Flow<Boolean> = context.dataStore.data
        .map { prefs -> !prefs[accessToken].isNullOrEmpty() }

    private val _sessionExpired = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val sessionExpired: SharedFlow<Unit> = _sessionExpired

    suspend fun notifySessionExpired() {
        _sessionExpired.emit(Unit)
    }

    suspend fun updatePreferencesKey(key : String, value : String) {
        context.dataStore.edit { prefs ->
            prefs[stringPreferencesKey(key)] = value
        }
    }

    fun getTokens(prefKey : String) : Flow<String> {
        return context.dataStore.data.map { prefs ->
            prefs[stringPreferencesKey(prefKey)] ?: ""
        }
    }

    suspend fun getRefreshToken(): String = context.dataStore.data.first()[refreshToken] ?: ""

    suspend fun getAccessToken(): String = context.dataStore.data.first()[accessToken] ?: ""

    suspend fun updateSessionDetails(
        accessToken: String,
        refreshToken: String,
        kdfSalt: String,
        kdfParams: String,
        wrappedVaultKey: String,
        vaultKeyNonce: String
    ){
        context.dataStore.edit { prefs ->
            prefs[this.accessToken] = accessToken
            prefs[this.refreshToken] = refreshToken
            prefs[this.kdfSalt] = kdfSalt
            prefs[this.kdfParams] = kdfParams
            prefs[this.wrappedVaultKey] = wrappedVaultKey
            prefs[this.vaultKeyNonce] = vaultKeyNonce
        }
    }

    suspend fun updateTokens(accessToken: String, refreshToken: String) {
        context.dataStore.edit { prefs ->
            prefs[this.accessToken] = accessToken
            prefs[this.refreshToken] = refreshToken
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }
}
