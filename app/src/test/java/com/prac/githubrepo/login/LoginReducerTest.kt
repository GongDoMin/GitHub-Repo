package com.prac.githubrepo.login

import com.prac.githubrepo.ui.login.LoginReducer
import com.prac.githubrepo.ui.login.model.Mutation
import com.prac.githubrepo.ui.login.view.UiState
import org.junit.Assert.assertFalse
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LoginReducerTest {

    private val loginReducerTest = LoginReducer()

    @Test
    fun invoke_mutationIsShowIdle_uiStateIsIdle() {

        val result = loginReducerTest.invoke(Mutation.ShowIdle, UiState())

        assertFalse(result.isLoading)
        assertFalse(result.isError)
        assertTrue(result.errorMessage.isEmpty())
    }

    @Test
    fun invoke_mutationIsShowLoading_uiStateIsLoading() {

        val result = loginReducerTest.invoke(Mutation.ShowLoading, UiState())

        assertTrue(result.isLoading)
        assertFalse(result.isError)
        assertTrue(result.errorMessage.isEmpty())
    }

    @Test
    fun invoke_mutationIsShowError_uiStateIsError() {
        val errorMessage = "test"

        val result = loginReducerTest.invoke(Mutation.ShowError(errorMessage), UiState())

        assertFalse(result.isLoading)
        assertTrue(result.isError)
        assertEquals(result.errorMessage, errorMessage)
    }
}