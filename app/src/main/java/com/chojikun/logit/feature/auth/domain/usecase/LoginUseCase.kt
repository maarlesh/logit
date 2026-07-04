package com.chojikun.logit.feature.auth.domain.usecase

import com.chojikun.logit.core.network.util.ApiResult
import com.chojikun.logit.core.util.CryptoHelper
import com.chojikun.logit.feature.auth.data.model.LoginPayload
import com.chojikun.logit.feature.auth.domain.model.LoginResult
import com.chojikun.logit.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(email: String, password: String): ApiResult<LoginResult> {
        val kdfLookup = when (val result = repository.getKdfParams(email)) {
            is ApiResult.Success -> result.data
            is ApiResult.Error -> return ApiResult.Error(
                message = if (result.code == 404) "No account with that email" else result.message,
                code = result.code,
            )
        }

        val authKey = withContext(Dispatchers.Default) {
            CryptoHelper.deriveAuthKey(password, kdfLookup.kdfSalt, kdfLookup.kdfParams)
        }
        return repository.login(LoginPayload(email, authKey))
    }
}
