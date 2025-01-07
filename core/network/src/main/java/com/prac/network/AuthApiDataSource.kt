package com.prac.network

import com.prac.network.model.response.TokenResponse

interface AuthApiDataSource {
    suspend fun authorizeOAuth(code: String) : TokenResponse

    suspend fun refreshAccessToken(refreshToken: String) : TokenResponse
}