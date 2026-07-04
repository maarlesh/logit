package com.chojikun.logit.feature.auth.data.repository

import androidx.annotation.Nullable
import com.chojikun.logit.core.network.util.ApiResult
import com.chojikun.logit.feature.auth.data.model.LoginPayload
import com.chojikun.logit.feature.auth.data.model.RegisterPayload
import com.chojikun.logit.feature.auth.data.remote.AuthApi
import com.chojikun.logit.feature.auth.domain.model.KdfLookupResult
import com.chojikun.logit.feature.auth.domain.model.LoginResult
import com.chojikun.logit.feature.auth.domain.model.RegisterResult
import com.chojikun.logit.feature.auth.domain.repository.AuthRepository
import com.google.gson.JsonParser
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
) : AuthRepository {

    override suspend fun register(payload: RegisterPayload): ApiResult<RegisterResult> =
        safeCall {
            val data = api.register(payload).data
            RegisterResult(
                accessToken = data.accessToken,
                refreshToken = data.refreshToken,
                kdfSalt = data.kdfSalt,
                kdfParams = data.kdfParams,
                wrappedVaultKey = data.wrappedVaultKey,
                vaultKeyNonce = data.vaultKeyNonce,
            )
        }

    override suspend fun getKdfParams(email: String): ApiResult<KdfLookupResult> =
        safeCall {
            val data = api.getKdfParams(email).data
            KdfLookupResult(kdfSalt = data.kdfSalt, kdfParams = data.kdfParams)
        }

    override suspend fun login(payload: LoginPayload): ApiResult<LoginResult> =
        safeCall {
            val response = api.login(payload)
            val data = response.data
            LoginResult(
                accessToken = data.accessToken,
                refreshToken = data.refreshToken,
                kdfSalt = data.kdfSalt,
                kdfParams = data.kdfParams,
                wrappedVaultKey = data.wrappedVaultKey,
                vaultKeyNonce = data.vaultKeyNonce,
            )
        }

    override suspend fun logout(payload : String) : ApiResult<Nullable> =
        safeCall {
            val response = api.logout(payload)
            response.data
        }

    private suspend fun <T> safeCall(call: suspend () -> T): ApiResult<T> =
        try {
            ApiResult.Success(call())
        } catch (e: HttpException) {
            val bodyMessage = e.response()?.errorBody()?.string()?.let { body ->
                runCatching { JsonParser.parseString(body).asJsonObject.get("message")?.asString }.getOrNull()
            }
            ApiResult.Error(bodyMessage ?: e.message(), e.code())
        } catch (e: IOException) {
            ApiResult.Error("Network error: ${e.message}")
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Unknown error")
        }
}
