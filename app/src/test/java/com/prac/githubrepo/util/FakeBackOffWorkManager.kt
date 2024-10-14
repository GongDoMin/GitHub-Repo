package com.prac.githubrepo.util

import com.prac.data.exception.CommonException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException

class FakeBackOffWorkManager : BackOffWorkManager {
    private val _workMap = mutableMapOf<String, Job>()
    private val _delayTimesMap = mutableMapOf<String, Long>()

    private lateinit var scope: CoroutineScope

    override fun addWork(
        uniqueID: String,
        times: Int,
        initialDelay: Long,
        maxDelay: Long,
        factor: Double,
        work: suspend () -> Result<*>
    ) {
        if (!::scope.isInitialized) {
            throw Exception("scope not initialized")
        }

        removeWork(uniqueID)
        _delayTimesMap.remove(uniqueID)
        val maxTimes = times.coerceAtMost(5)

        var currentDelay = initialDelay

        val job = scope.launch {
            repeat(maxTimes) {
                work()
                    .onSuccess {
                        removeWork(uniqueID)
                        return@launch
                    }
                    .onFailure {
                        if (it !is CommonException.NetworkError) {
                            removeWork(uniqueID)
                            return@launch
                        }
                    }
                _delayTimesMap[uniqueID] = (_delayTimesMap[uniqueID] ?: 0L) + currentDelay
                currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
            }
            removeWork(uniqueID)
        }

        _workMap[uniqueID] = job
    }

    override fun clearWork() {
        _workMap.clear()
    }

    fun setScope(scope: CoroutineScope) {
        this.scope = scope
    }

    fun clearDelayTimes() {
        _delayTimesMap.clear()
    }

    fun getDelayTimes(uniqueID: String) : Long =
        _delayTimesMap[uniqueID] ?: 0L

    private fun removeWork(uniqueID: String) {
        _workMap[uniqueID]?.cancel()
        _workMap.remove(uniqueID)
    }
}