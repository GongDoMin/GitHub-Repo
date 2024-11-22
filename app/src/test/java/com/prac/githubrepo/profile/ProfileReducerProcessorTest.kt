package com.prac.githubrepo.profile

import com.prac.githubrepo.ui.profile.ProfileReducerProcessor
import com.prac.githubrepo.ui.profile.model.Mutation.ShowDialog
import com.prac.githubrepo.ui.profile.model.Mutation.ShowIdle
import com.prac.githubrepo.ui.profile.model.Mutation.ShowLoading
import com.prac.githubrepo.ui.profile.view.UiState
import org.junit.Assert.assertFalse
import org.junit.Test
import kotlin.test.assertTrue

class ProfileReducerProcessorTest {

    private val profileReducerProcessorTest = ProfileReducerProcessor()

    @Test
    fun invoke_mutationIsShowIdle_uiStateIsIdle() {

        val result = profileReducerProcessorTest.invoke(ShowIdle, UiState())

        assertFalse(result.isLoading)
        assertFalse(result.isDialog)
    }

    @Test
    fun invoke_mutationIsShowLoading_uiStateIsLoading() {

        val result = profileReducerProcessorTest.invoke(ShowLoading, UiState())

        assertTrue(result.isLoading)
        assertFalse(result.isDialog)
    }

    @Test
    fun invoke_mutationIsShowError_uiStateIsError() {

        val result = profileReducerProcessorTest.invoke(ShowDialog, UiState())

        assertFalse(result.isLoading)
        assertTrue(result.isDialog)
    }
}