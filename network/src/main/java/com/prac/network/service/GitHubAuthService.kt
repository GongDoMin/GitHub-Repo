package com.prac.network.service

import com.prac.network.BuildConfig
import com.prac.network.dto.TokenDto
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

internal interface GitHubAuthService {
    @POST("login/oauth/access_token")
    suspend fun authorizeOAuth(
        @Header("Accept") accept: String = "application/json",
        @Query("client_id") clientID: String = BuildConfig.CLIENT_ID,
        @Query("client_secret") clientSecret: String = BuildConfig.CLIENT_SECRET,
        @Query("code") code: String,
    ): TokenDto

    @POST("login/oauth/access_token")
    suspend fun refreshAccessToken(
        @Header("Accept") accept: String = "application/json",
        @Query("client_id") clientID: String = BuildConfig.CLIENT_ID,
        @Query("client_secret") clientSecret: String = BuildConfig.CLIENT_SECRET,
        @Query("grant_type") grantType: String = "refresh_token",
        @Query("refresh_token") refreshToken: String
    ): TokenDto
}