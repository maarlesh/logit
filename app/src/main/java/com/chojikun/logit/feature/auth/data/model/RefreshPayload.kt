package com.chojikun.logit.feature.auth.data.model

data class RefreshPayload(
    val accessToken: String,
    val refreshToken: String,
)
