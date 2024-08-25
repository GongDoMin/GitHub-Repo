package com.prac.githubrepo.main

import android.view.View
import com.prac.githubrepo.main.request.Request

class RepoStarUpdater(
    private val request: Request,
    private val view: View
) {
    private var isAttachedStateListenerAdded = false
    private val attachedStateListener = object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(view: View) {

        }

        override fun onViewDetachedFromWindow(view: View) {

        }
    }
}