package com.chojikun.logit.feature.auth.domain.model

data class RegisterResult(
    val accessToken: String,
    val refreshToken: String,
    val kdfSalt: String,
    val kdfParams: KdfParams,
    val wrappedVaultKey: String,
    val vaultKeyNonce: String,
)
