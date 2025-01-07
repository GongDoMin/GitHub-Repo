package com.prac.local.impl

import com.prac.local.TokenLocalDataSource
import com.prac.local.datastore.token.TokenDataStoreManager
import com.prac.local.model.TokenEntity
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject

internal class TokenLocalDataSourceImpl @Inject constructor(
    private val tokenDataStoreManager: TokenDataStoreManager
) : TokenLocalDataSource {

    private val cachedToken: AtomicReference<TokenEntity> = AtomicReference()

    init {
        runBlocking {
            val token = tokenDataStoreManager.getToken()

            cachedToken.set(token)
        }
    }

    override suspend fun setToken(token: TokenEntity) {
        tokenDataStoreManager.setToken(token)
        updateToken(token)
    }

    override fun getToken(): TokenEntity {
        return cachedToken.get()
    }

    private fun updateToken(newToken: TokenEntity) {
        cachedToken.set(newToken)
    }

    override suspend fun clearToken() {
        tokenDataStoreManager.clearToken()

        val token = tokenDataStoreManager.getToken()
        cachedToken.set(token)
    }
}