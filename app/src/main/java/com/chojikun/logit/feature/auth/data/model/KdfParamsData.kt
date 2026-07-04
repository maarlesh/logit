package com.chojikun.logit.feature.auth.data.model

import com.chojikun.logit.feature.auth.domain.model.KdfParams

data class KdfParamsData(
    val kdfSalt: String,
    val kdfParams: KdfParams,
)
