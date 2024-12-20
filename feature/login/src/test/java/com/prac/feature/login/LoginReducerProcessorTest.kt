package com.prac.feature.login

import com.prac.feature.login.model.Mutation
import com.prac.feature.login.view.UiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LoginReducerProcessorTest {

    private val loginReducerProcessorTest = LoginReducerProcessor()

    @Test
    fun invoke_mutationIsShowIdle_uiStateIsIdle() {

        val result = loginReducerProcessorTest.invoke(
            Mutation.ShowIdle,
            UiState.Idle
        )

        assertFalse(result is UiState.Loading)
        assertFalse(result is UiState.Error)
    }

    @Test
    fun invoke_mutationIsShowLoading_uiStateIsLoading() {

        val result = loginReducerProcessorTest.invoke(
            Mutation.ShowLoading,
            UiState.Idle
        )

        assertTrue(result is UiState.Loading)
        assertFalse(result is UiState.Error)
    }

    @Test
    fun invoke_mutationIsShowError_uiStateIsError() {
        val errorMessage = "test"

        val result = loginReducerProcessorTest.invoke(
            Mutation.ShowError(errorMessage),
            UiState.Idle
        )

        assertFalse(result is UiState.Loading)
        assertTrue(result is UiState.Error)
    }
}