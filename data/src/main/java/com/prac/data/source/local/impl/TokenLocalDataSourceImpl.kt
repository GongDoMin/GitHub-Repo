package com.prac.data.source.local.impl

import com.prac.data.repository.model.TokenModel
import com.prac.data.source.local.datastore.TokenDataStoreManager
import com.prac.data.source.local.TokenLocalDataSource
import com.prac.data.source.local.datastore.TokenLocalDto
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject

internal class TokenLocalDataSourceImpl @Inject constructor(
    private val tokenDataStoreManager: TokenDataStoreManager
) : TokenLocalDataSource {

    private val cachedToken: AtomicReference<TokenLocalDto> = AtomicReference()

    init {
        runBlocking {
            val token = tokenDataStoreManager.getToken()

            cachedToken.set(token)
        }
    }

    override suspend fun setToken(token: TokenLocalDto) {
        tokenDataStoreManager.saveTokenData(token)
        updateToken(token)
    }

    override fun getToken(): TokenLocalDto {
        return cachedToken.get()
    }

    private fun updateToken(newToken: TokenLocalDto) {
        cachedToken.set(newToken)
    }

    override suspend fun clearToken() {
        tokenDataStoreManager.clearToken()

        val token = tokenDataStoreManager.getToken()
        cachedToken.set(token)
    }
}