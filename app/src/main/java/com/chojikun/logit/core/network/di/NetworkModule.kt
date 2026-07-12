package com.chojikun.logit.core.network.di

import com.chojikun.logit.BuildConfig
import com.chojikun.logit.core.network.AuthAuthenticator
import com.chojikun.logit.core.util.SessionManager
import com.chojikun.logit.feature.auth.data.remote.AuthApi
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://logit-backend-194h.onrender.com/"

    // Endpoints that must not carry a (possibly missing/stale) access token.
    private val noAuthPaths = setOf(
        "auth/register",
        "auth/login",
        "auth/kdf-params",
        "auth/refresh",
    )

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .create()

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }

    @Provides
    @Singleton
    fun provideAuthHeaderInterceptor(sessionManager: SessionManager): Interceptor =
        Interceptor { chain ->
            val original = chain.request()
            val path = original.url.encodedPath.removePrefix("/")
            if (path in noAuthPaths) {
                return@Interceptor chain.proceed(original)
            }

            val accessToken = runBlocking { sessionManager.getAccessToken() }
            val request = if (accessToken.isNotEmpty()) {
                original.newBuilder()
                    .header("Authorization", "Bearer $accessToken")
                    .build()
            } else {
                original
            }
            chain.proceed(request)
        }

    // Bare client used only by AuthAuthenticator to call auth/refresh — it must not carry the
    // Authenticator itself, or a failed refresh would recursively trigger another refresh attempt.
    @Provides
    @Singleton
    @RefreshApi
    fun provideRefreshOkHttpClient(logging: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

    @Provides
    @Singleton
    @RefreshApi
    fun provideRefreshRetrofit(@RefreshApi okHttpClient: OkHttpClient, gson: Gson): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    @Provides
    @Singleton
    @RefreshApi
    fun provideRefreshAuthApi(@RefreshApi retrofit: Retrofit): AuthApi =
        retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideOkHttpClient(
        logging: HttpLoggingInterceptor,
        authHeaderInterceptor: Interceptor,
        authAuthenticator: AuthAuthenticator,
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(authHeaderInterceptor)
            .addInterceptor(logging)
            .authenticator(authAuthenticator)
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi =
        retrofit.create(AuthApi::class.java)
}
