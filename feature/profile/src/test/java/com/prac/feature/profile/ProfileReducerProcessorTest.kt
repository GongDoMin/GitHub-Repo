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
    fun 아이들상태_변환테스트() {
        // when
        val result = profileReducerProcessorTest.invoke(ShowIdle, UiState.Loading)

        // then
        assertTrue(result is UiState.Idle)
    }

    @Test
    fun 로딩상태_변환테스트() {
        // when
        val result = profileReducerProcessorTest.invoke(ShowLoading, UiState.Idle)

        // then
        assertTrue(result is UiState.Loading)
    }

    @Test
    fun 다이얼로그상태_변환테스트() {
        // when
        val result = profileReducerProcessorTest.invoke(ShowDialog, UiState.Loading)

        // then
        assertTrue(result is UiState.Dialog)
    }
}