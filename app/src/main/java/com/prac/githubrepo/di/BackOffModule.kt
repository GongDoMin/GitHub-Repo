package com.prac.githubrepo.di

import com.prac.data.exception.CommonException
import com.prac.githubrepo.util.BackOffWorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class BackOffModule {
    @Provides
    @Singleton
    fun provideBackOffWorkManager() : BackOffWorkManager {
        return object : BackOffWorkManager {
            private val _workMap = mutableMapOf<String, Job>()

            private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

            override fun addWork(
                uniqueID: String,
                times: Int,
                initialDelay: Long,
                maxDelay: Long,
                factor: Double,
                work: suspend () -> Result<*>
            ) {
                _workMap[uniqueID]?.cancel()

                var currentDelay = initialDelay

                val job = coroutineScope.launch {
                    repeat(times - 1) {
                        work()
                            .onSuccess {
                                cancelAndRemoveJob(uniqueID)
                                return@launch
                            }
                            .onFailure {
                                if (it !is CommonException) {
                                    cancelAndRemoveJob(uniqueID)
                                    return@launch
                                }
                            }

                        delay(currentDelay)
                        currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
                    }

                    cancelAndRemoveJob(uniqueID)
                }

                _workMap[uniqueID] = job
            }

            override fun clearWork() {
                _workMap.forEach { if (!it.value.isCompleted) it.value.cancel() }

                _workMap.clear()
            }

            private fun cancelAndRemoveJob(uniqueID: String) {
                _workMap[uniqueID]?.run {
                    cancel()
                    _workMap.remove(uniqueID)
                }
            }
        }
    }
}