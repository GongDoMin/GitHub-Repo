package com.prac.shared_test.auth

import com.prac.auth.AuthManager
import com.prac.auth.model.TokenModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicInteger

class FakeAuthManager(
    private var accessToken: String = "",
    private val maxAllowedCallsBeforeExpiry: Int = 0,
    private val isRefreshTokenExpired: Boolean = false,
    private val refreshError: Throwable? = null
) : AuthManager {
    private val callTimes = AtomicInteger(0)
    private var accessTokenIsRefreshed = false
    var refreshCallTimes = 0
        private set

    override fun getAccessToken(refreshAccessToken: suspend (refreshToken: String) -> TokenModel): String {
        if (isAccessTokenIsExpired()) {
            synchronized(this) {
                if (!accessTokenIsRefreshed) {
                    if (isRefreshTokenIsExpired()) {
                        runBlocking {
                            clearToken()
                        }
                        return@synchronized
                    }

                    runBlocking {
                        val response = refreshAccessToken("")

                        accessTokenIsRefreshed = true
                        refreshCallTimes++

                        refreshError?.let {
                            clearToken()
                            return@runBlocking
                        }

                        simulateNetworkDelay()
                        accessToken = response.accessToken
                    }
                }
            }
        }

        return accessToken
    }

    private fun isAccessTokenIsExpired() : Boolean {
        val currentCallCount = callTimes.incrementAndGet()
        return currentCallCount > maxAllowedCallsBeforeExpiry
    }

    private fun isRefreshTokenIsExpired() : Boolean = isRefreshTokenExpired

    private fun clearToken() {
        runBlocking {
            accessToken = ""
        }
    }

    private suspend fun simulateNetworkDelay() {
        delay(300) // Simulates network latency
    }
}