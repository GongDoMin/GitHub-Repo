package com.prac.githubrepo.main.request

import com.prac.data.entity.RepoEntity
import com.prac.githubrepo.main.star.StarStateFetcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

internal class StarStateRequestImpl internal constructor(
    private val starStateFetcher: StarStateFetcher,
    private val repoEntity: RepoEntity,
    private val scope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher
) : StarStateRequest {
    override fun fetchStarState() {
        scope.launch(ioDispatcher) {
            starStateFetcher.fetchStarState(repoEntity)
        }
    }
}