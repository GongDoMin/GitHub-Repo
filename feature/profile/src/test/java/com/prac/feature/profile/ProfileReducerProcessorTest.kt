package com.prac.feature.profile

import com.prac.feature.profile.model.Mutation.ShowDialog
import com.prac.feature.profile.model.Mutation.ShowIdle
import com.prac.feature.profile.model.Mutation.ShowLoading
import com.prac.feature.profile.view.UiState
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProfileReducerProcessorTest {

    private val profileReducerProcessorTest = ProfileReducerProcessor()

    @Test
    fun invoke_mutationIsShowIdle_uiStateIsIdle() {

        val result = profileReducerProcessorTest.invoke(ShowIdle, UiState.Idle)

        assertTrue(result is UiState.Idle)
    }

    @Test
    fun invoke_mutationIsShowLoading_uiStateIsLoading() {

        val result = profileReducerProcessorTest.invoke(ShowLoading, UiState.Idle)

        assertTrue(result is UiState.Loading)
    }

    @Test
    fun invoke_mutationIsShowError_uiStateIsError() {

        val result = profileReducerProcessorTest.invoke(ShowDialog, UiState.Loading)

        assertTrue(result is UiState.Dialog)
    }
}