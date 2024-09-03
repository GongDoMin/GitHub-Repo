package com.prac.githubrepo.main.request

import android.view.View
import com.prac.data.entity.RepoEntity
import com.prac.githubrepo.R
import com.prac.githubrepo.main.RepoStarUpdater
import com.prac.githubrepo.main.star.StarStateFetcher
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope

class StarStateRequestBuilder @AssistedInject constructor(
    private val starStateFetcher: StarStateFetcher,
    @Assisted private val scope: CoroutineScope,
) {
    companion object {
        private val tagID = R.string.requestID
    }

    @AssistedFactory
    interface Factory {
        fun create(scope: CoroutineScope): StarStateRequestBuilder
    }

    private var view: View? = null
    private var repoEntity: RepoEntity? = null

    fun setView(view: View) : StarStateRequestBuilder = apply {
        this.view = view
    }

    fun setRepoEntity(repoEntity: RepoEntity) = apply {
        this.repoEntity = repoEntity
    }

    fun build() {
        val view = checkNotNull(view)
        val repoEntity = checkNotNull(repoEntity)

        val updater = RepoStarUpdater(
            starStateRequest = StarStateRequestImpl(
                starStateFetcher = starStateFetcher,
                repoEntity = repoEntity,
                scope = scope
            ),
            view = view
        )

        if (view.hasUpdaterTag()) (view.getTag(tagID) as RepoStarUpdater).removeListener()

        view.setTag(tagID, updater)

        if (repoEntity.isStarred == null) updater.addListener()

        clear()
    }

    private fun View.hasUpdaterTag() : Boolean {
        val tag = getTag(tagID) ?: return false

        if (tag !is RepoStarUpdater) throw IllegalStateException("Tag is not of type RepoStarUpdater")

        return true
    }

    private fun clear() = apply {
        this.view = null
        this.repoEntity = null
    }
}