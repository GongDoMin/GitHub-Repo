package com.prac.auth

import com.prac.auth.model.TokenModel
import com.prac.shared_test.auth.FakeAuthManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class AuthManagerTest {

    private lateinit var authManager: FakeAuthManager
    private val accessToken = "accessToken"
    private val refreshToken = "refreshToken"

    @Test
    fun getAccessToken_oneRequest_whenAccessTokenIsNotExpired_returnAccessToken() {
        authManager = FakeAuthManager(
            accessToken = accessToken,
            maxAllowedCallsBeforeExpiry = 1
        )

        val result = authManager.getAccessToken { TokenModel(accessToken = refreshToken) }

        assertEquals(result, accessToken)
        assertEquals(authManager.refreshCallTimes, 0)
    }

    @Test
    fun getAccessToken_oneRequest_whenAccessTokenIsExpired_refreshIsCalled() {
        authManager = FakeAuthManager(
            accessToken = accessToken,
            maxAllowedCallsBeforeExpiry = 0
        )

        val result = authManager.getAccessToken { TokenModel(accessToken = refreshToken) }

        assertEquals(result, refreshToken)
        assertEquals(authManager.refreshCallTimes, 1)
    }

    @Test
    fun getAccessToken_oneRequest_whenAccessTokenIsExpired_andRefreshTokenIsError_tokenIsCleared() {
        authManager = FakeAuthManager(
            accessToken = accessToken,
            maxAllowedCallsBeforeExpiry = 0,
            refreshError = IOException()
        )

        val result = authManager.getAccessToken { TokenModel(accessToken = refreshToken) }

        assertTrue(result.isEmpty())
        assertEquals(authManager.refreshCallTimes, 1)
    }

    @Test
    fun getAccessToken_oneRequest_whenAccessTokenIsExpired_tokenIsCleared() {
        authManager = FakeAuthManager(
            accessToken = accessToken,
            maxAllowedCallsBeforeExpiry = 0,
            isRefreshTokenExpired = true
        )

        val result = authManager.getAccessToken { TokenModel(accessToken = refreshToken) }

        assertTrue(result.isEmpty())
        assertEquals(authManager.refreshCallTimes, 0)
    }

    @Test
    fun getAccessToken_multipleRequest_whenAccessTokenIsNotExpired_refreshIsNotCalled() = runTest {
        authManager = FakeAuthManager(
            accessToken = accessToken,
            maxAllowedCallsBeforeExpiry = 6
        )

        for (i in 1..5) {
            launch {
                authManager.getAccessToken { TokenModel(accessToken = refreshToken) }
            }.join()
        }

        val result = authManager.getAccessToken { TokenModel(accessToken = refreshToken) }
        assertEquals(result, accessToken)
        assertEquals(authManager.refreshCallTimes, 0)
    }

    @Test
    fun getAccessToken_multipleRequest_whenAccessTokenIsExpired_refreshIsCalled() = runTest {
        authManager = FakeAuthManager(
            accessToken = accessToken,
            maxAllowedCallsBeforeExpiry = 3
        )

        for (i in 1..5) {
            launch {
                authManager.getAccessToken { TokenModel(accessToken = refreshToken) }
            }.join()
        }

        val result = authManager.getAccessToken { TokenModel(accessToken = refreshToken) }
        assertEquals(result, refreshToken)
        assertEquals(authManager.refreshCallTimes, 1)
    }

    @Test
    fun getAccessToken_multipleRequest_whenRefreshTokenIsExpired_refreshIsCleared() = runTest {
        authManager = FakeAuthManager(
            accessToken = accessToken,
            maxAllowedCallsBeforeExpiry = 0,
            isRefreshTokenExpired = true
        )

        for (i in 1..5) {
            launch {
                authManager.getAccessToken { TokenModel(accessToken = refreshToken) }
            }.join()
        }

        val result = authManager.getAccessToken { TokenModel(accessToken = refreshToken) }
        assertTrue(result.isEmpty())
        assertEquals(authManager.refreshCallTimes, 0)
    }

    @Test
    fun getAccessToken_multipleRequest_whenRefreshTokenIsExpired_andRefreshTokenIsError_refreshIsCleared() = runTest {
        authManager = FakeAuthManager(
            accessToken = accessToken,
            maxAllowedCallsBeforeExpiry = 3,
            isRefreshTokenExpired = true,
            refreshError = IOException()
        )

        for (i in 1..5) {
            launch {
                authManager.getAccessToken { TokenModel(accessToken = refreshToken) }
            }.join()
        }

        val result = authManager.getAccessToken { TokenModel(accessToken = refreshToken) }
        assertTrue(result.isEmpty())
        assertEquals(authManager.refreshCallTimes, 0)
    }
}