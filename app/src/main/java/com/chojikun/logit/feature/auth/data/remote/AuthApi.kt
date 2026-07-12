package com.chojikun.logit.feature.auth.data.remote

import androidx.annotation.Nullable
import com.chojikun.logit.feature.auth.data.model.ApiResponse
import com.chojikun.logit.feature.auth.data.model.KdfParamsData
import com.chojikun.logit.feature.auth.data.model.LoginData
import com.chojikun.logit.feature.auth.data.model.LoginPayload
import com.chojikun.logit.feature.auth.data.model.RegisterData
import com.chojikun.logit.feature.auth.data.model.RegisterPayload
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApi {
    @POST("auth/register")
    suspend fun register(@Body payload: RegisterPayload): ApiResponse<RegisterData>

    @GET("auth/kdf-params")
    suspend fun getKdfParams(@Query("email") email: String): ApiResponse<KdfParamsData>

    @POST("auth/login")
    suspend fun login(@Body payload: LoginPayload): ApiResponse<LoginData>

    @POST("auth/logout")
    suspend fun logout(
        @Header("Authorization") accessToken: String,
        @Body refreshToken: String
    ): ApiResponse<Nullable>
}