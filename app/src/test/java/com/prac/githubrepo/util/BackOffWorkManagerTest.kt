package com.prac.githubrepo.util

import com.prac.data.exception.CommonException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class BackOffWorkManagerTest {

    private lateinit var backOffWorkManager: FakeBackOffWorkManager

    @Before
    fun setUp() {
        backOffWorkManager = FakeBackOffWorkManager()
    }

    @After
    fun tearDown() {
        backOffWorkManager.clearWork()
        backOffWorkManager.clearDelayTimes()
    }
    @Test
    fun addWork_retryUntilSuccess() = runTest {
        var retryTimes = 0
        val maxRetryTimes = 3
        val delayTimes = 7_000L // 1초 -> 2초 -> 4초 = 7초
        val uniqueID = "testID"
        backOffWorkManager.setScope(this)

        backOffWorkManager.addWork(
            uniqueID = uniqueID,
            work = {
                if (retryTimes < maxRetryTimes) {
                    retryTimes++
                    Result.failure<Unit>(CommonException.NetworkError())
                } else {
                    Result.success("success")
                }
            }
        )
        advanceUntilIdle()

        assertEquals(retryTimes, maxRetryTimes)
        assertEquals(backOffWorkManager.getDelayTimes(uniqueID), delayTimes)
    }

    @Test
    fun addWork_cancelExistingWork_whenNewWorkIsAdded() = runTest {
        var fRetryTimes = 0
        val fMaxRetryTimes = 3
        val uniqueID = "testID"
        backOffWorkManager.setScope(this)
        backOffWorkManager.addWork(
            uniqueID = uniqueID,
            work = {
                if (fRetryTimes < fMaxRetryTimes) {
                    fRetryTimes++
                    Result.failure<Unit>(CommonException.NetworkError())
                } else {
                    Result.success("success")
                }
            }
        )
        var sRetryTimes = 0
        val sMaxRetryTimes = 4
        val sDelayTimes = 15_000L // 1초 -> 2초 -> 4초 -> 8초 = 15초

        backOffWorkManager.addWork(
            uniqueID = uniqueID,
            work = {
                if (sRetryTimes < sMaxRetryTimes) {
                    sRetryTimes++
                    Result.failure<Unit>(CommonException.NetworkError())
                } else {
                    Result.success("success")
                }
            }
        )
        advanceUntilIdle()

        Assert.assertNotEquals(fRetryTimes, fMaxRetryTimes)
        assertEquals(sRetryTimes, sMaxRetryTimes)
        assertEquals(backOffWorkManager.getDelayTimes(uniqueID), sDelayTimes)
    }

    @Test
    fun addWork_stopRetrying_onNonIOException() = runTest {
        var retryTimes = 0
        val maxRetryTimes = 3
        val delayTimes = 7_000L // 1초 -> 2초 -> 4초 = 7초
        val uniqueID = "testID"
        backOffWorkManager.setScope(this)

        backOffWorkManager.addWork(
            uniqueID = uniqueID,
            work = {
                if (retryTimes < maxRetryTimes) {
                    retryTimes++
                    Result.failure<Unit>(CommonException.NetworkError())
                } else {
                    Result.failure<Unit>(IllegalArgumentException()) // IOException 이 아닌 다른 에러
                }
            }
        )

        advanceUntilIdle()
        assertEquals(retryTimes, maxRetryTimes)
        assertEquals(backOffWorkManager.getDelayTimes(uniqueID), delayTimes)
    }
}