package com.prac.network

import com.prac.network.dto.TokenDto

interface AuthApiDataSource {
    suspend fun authorizeOAuth(code: String) : TokenDto

    suspend fun refreshAccessToken(refreshToken: String) : TokenDto
}