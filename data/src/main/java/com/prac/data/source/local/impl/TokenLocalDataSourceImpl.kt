package com.prac.data.source.local.impl

import com.prac.data.source.local.datastore.TokenDataStoreManager
import com.prac.data.repository.model.TokenModel
import com.prac.data.source.local.TokenLocalDataSource
import com.prac.data.source.local.datastore.TokenLocalDto
import javax.inject.Inject

internal class TokenLocalDataSourceImpl @Inject constructor(
    private val tokenDataStoreManager: TokenDataStoreManager
) : TokenLocalDataSource {
    override suspend fun setToken(token: TokenLocalDto) {
        tokenDataStoreManager.saveTokenData(token)
    }

    override suspend fun getToken(): TokenLocalDto {
        return tokenDataStoreManager.getToken()
    }
}