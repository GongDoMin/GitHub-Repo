package com.prac.githubrepo.main.request

import com.prac.data.entity.RepoEntity
import com.prac.data.repository.RepoRepository
import com.prac.githubrepo.main.StarStateUpdater
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class StarRequest internal constructor(
    private val repoRepository: RepoRepository,
    private val starStateUpdater: StarStateUpdater,
    private val repoEntity: RepoEntity,
    private val scope: CoroutineScope,
) : Request {
    private var job: Job? = null

    override fun checkStarredState() {
        cancel()

        job = scope.launch(Dispatchers.IO) {
            repoRepository.isStarred(repoEntity.name)
                .onSuccess {
                    starStateUpdater.updateStarState(repoEntity.id, it)
                }.onFailure {
                    starStateUpdater.updateStarState(repoEntity.id, false)
                }
        }
    }

    override fun cancel() {
        job?.let {
            if (!isCompleted()) it.cancel()
            job = null
        }
    }

    override fun isCompleted() : Boolean {
        return job?.isCompleted == true
    }
}