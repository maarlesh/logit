package com.chojikun.logit.feature.auth.domain.model

data class KdfLookupResult(
    val kdfSalt: String,
    val kdfParams: KdfParams,
)
