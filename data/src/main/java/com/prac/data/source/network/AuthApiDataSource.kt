package com.prac.data.source.network

import com.prac.data.repository.model.TokenModel

internal interface AuthApiDataSource {
    suspend fun authorizeOAuth(code: String) : TokenModel

    suspend fun refreshAccessToken(refreshToken: String) : TokenModel
}