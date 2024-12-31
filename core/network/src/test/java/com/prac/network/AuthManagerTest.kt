package com.prac.network

import com.prac.shared_test.network.service.FakeAuthManager
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class AuthManagerTest {

    private lateinit var authManager: FakeAuthManager
    private val accessToken = "accessToken"
    private val refreshToken = "refresh$accessToken"

    @Test
    fun checkTokenIsExpired_oneRequest_whenAccessTokenIsNotExpired_refreshIsNotCalled() {
        authManager = FakeAuthManager(
            accessToken = accessToken,
            maxAllowedCallsBeforeExpiry = 1
        )

        authManager.checkTokenIsExpired()

        assertEquals(authManager.getAccessToken(), accessToken)
        assertEquals(authManager.refreshCallTimes, 0)
    }

    @Test
    fun checkTokenIsExpired_oneRequest_whenAccessTokenIsExpired_refreshIsCalled() {
        authManager = FakeAuthManager(
            accessToken = accessToken,
            maxAllowedCallsBeforeExpiry = 0
        )

        authManager.checkTokenIsExpired()

        assertNotEquals(authManager.getAccessToken(), accessToken)
        assertEquals(authManager.getAccessToken(), refreshToken)
        assertEquals(authManager.refreshCallTimes, 1)
    }

    @Test
    fun checkTokenIsExpired_oneRequest_whenAccessTokenIsExpired_andRefreshTokenIsError_tokenIsCleared() {
        authManager = FakeAuthManager(
            accessToken = accessToken,
            maxAllowedCallsBeforeExpiry = 0,
            refreshError = IOException()
        )

        authManager.checkTokenIsExpired()

        assertTrue(authManager.getAccessToken().isEmpty())
        assertEquals(authManager.refreshCallTimes, 1)
    }

    @Test
    fun checkTokenIsExpired_oneRequest_whenAccessTokenIsExpired_tokenIsCleared() {
        authManager = FakeAuthManager(
            accessToken = accessToken,
            maxAllowedCallsBeforeExpiry = 0,
            isRefreshTokenExpired = true
        )

        authManager.checkTokenIsExpired()

        assertNotEquals(authManager.getAccessToken(), accessToken)
        assertTrue(authManager.getAccessToken().isEmpty())
        assertEquals(authManager.refreshCallTimes, 0)
    }

    @Test
    fun checkTokenIsExpired_multipleRequest_whenAccessTokenIsNotExpired_refreshIsNotCalled() = runTest {
        authManager = FakeAuthManager(
            accessToken = accessToken,
            maxAllowedCallsBeforeExpiry = 5
        )

        for (i in 1..5) {
            authManager.checkTokenIsExpired()
        }

        assertEquals(authManager.getAccessToken(), accessToken)
        assertEquals(authManager.refreshCallTimes, 0)
    }

    @Test
    fun checkTokenIsExpired_multipleRequest_whenAccessTokenIsExpired_refreshIsCalled() = runTest {
        authManager = FakeAuthManager(
            accessToken = accessToken,
            maxAllowedCallsBeforeExpiry = 3
        )

        for (i in 1..5) {
            authManager.checkTokenIsExpired()
        }

        assertNotEquals(authManager.getAccessToken(), accessToken)
        assertEquals(authManager.getAccessToken(), refreshToken)
        assertEquals(authManager.refreshCallTimes, 1)
    }

    @Test
    fun checkTokenIsExpired_multipleRequest_whenRefreshTokenIsExpired_refreshIsCleared() = runTest {
        authManager = FakeAuthManager(
            accessToken = accessToken,
            maxAllowedCallsBeforeExpiry = 0,
            isRefreshTokenExpired = true
        )

        for (i in 1..5) {
            authManager.checkTokenIsExpired()
        }

        assertNotEquals(authManager.getAccessToken(), accessToken)
        assertTrue(authManager.getAccessToken().isEmpty())
        assertEquals(authManager.refreshCallTimes, 0)
    }

    @Test
    fun checkTokenIsExpired_multipleRequest_whenRefreshTokenIsExpired_andRefreshTokenIsError_refreshIsCleared() = runTest {
        authManager = FakeAuthManager(
            accessToken = accessToken,
            maxAllowedCallsBeforeExpiry = 3,
            isRefreshTokenExpired = true,
            refreshError = IOException()
        )

        for (i in 1..5) {
            authManager.checkTokenIsExpired()
        }

        assertNotEquals(authManager.getAccessToken(), accessToken)
        assertTrue(authManager.getAccessToken().isEmpty())
        assertEquals(authManager.refreshCallTimes, 0)
    }
}