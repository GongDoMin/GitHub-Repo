package com.prac.data.source.network

import com.prac.data.repository.model.TokenModel

internal interface TokenApiDataSource {
    suspend fun getToken(
        code: String
    ) : TokenModel

    suspend fun refreshToken(refreshToken: String) : TokenModel
}