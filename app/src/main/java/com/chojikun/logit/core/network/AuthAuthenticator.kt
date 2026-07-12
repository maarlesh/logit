package com.chojikun.logit.core.network

import com.chojikun.logit.core.network.di.RefreshApi
import com.chojikun.logit.core.util.SessionManager
import com.chojikun.logit.feature.auth.data.model.RefreshPayload
import com.chojikun.logit.feature.auth.data.remote.AuthApi
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthAuthenticator @Inject constructor(
    private val sessionManager: SessionManager,
    @RefreshApi private val refreshAuthApi: AuthApi,
) : Authenticator {

    private val lock = Any()

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) return null

        val failedAccessToken = response.request.header("Authorization")?.removePrefix("Bearer ")

        return synchronized(lock) {
            val currentAccessToken = runBlocking { sessionManager.getAccessToken() }

            // Another in-flight request already refreshed the token; reuse it.
            if (currentAccessToken.isNotEmpty() && currentAccessToken != failedAccessToken) {
                return@synchronized response.request.newBuilder()
                    .header("Authorization", "Bearer $currentAccessToken")
                    .build()
            }

            val refreshToken = runBlocking { sessionManager.getRefreshToken() }
            if (refreshToken.isEmpty()) return@synchronized null

            val newTokens = runCatching {
                runBlocking {
                    refreshAuthApi.refresh(
                        RefreshPayload(accessToken = currentAccessToken, refreshToken = refreshToken)
                    ).data
                }
            }.getOrNull()

            if (newTokens == null) {
                runBlocking {
                    sessionManager.clearSession()
                    sessionManager.notifySessionExpired()
                }
                return@synchronized null
            }

            runBlocking { sessionManager.updateTokens(newTokens.accessToken, newTokens.refreshToken) }

            response.request.newBuilder()
                .header("Authorization", "Bearer ${newTokens.accessToken}")
                .build()
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}
