package com.chojikun.logit.feature.auth.domain.usecase

import androidx.annotation.Nullable
import com.chojikun.logit.core.network.util.ApiResult
import com.chojikun.logit.feature.auth.domain.repository.AuthRepository
import jakarta.inject.Inject

class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(): ApiResult<Nullable> = repository.logout()
}