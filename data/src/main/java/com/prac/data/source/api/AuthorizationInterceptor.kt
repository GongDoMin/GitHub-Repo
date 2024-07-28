package com.prac.data.source.api

import com.prac.data.di.datastore.TokenDataStoreManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject

class AuthorizationInterceptor @Inject constructor(
    private val tokenDataStoreManager: TokenDataStoreManager
) : Interceptor {
    companion object {
        private const val AUTHORIZATION = "Authorization"
        private const val AUTHORIZATION_TYPE = "Bearer"
    }

    private val accessToken = runBlocking {
        tokenDataStoreManager.getToken(TokenDataStoreManager.KEY.ACCESS_TOKEN)
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().putTokenHeader(accessToken)
        return chain.proceed(request)
    }

    private fun Request.putTokenHeader(accessToken: String) : Request {
        return this.newBuilder()
            .addHeader(AUTHORIZATION, "$AUTHORIZATION_TYPE $accessToken")
            .build()
    }
}