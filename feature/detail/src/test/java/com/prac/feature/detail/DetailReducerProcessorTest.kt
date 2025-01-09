package com.prac.feature.detail

import com.prac.data.model.RepositoryDetail
import com.prac.feature.detail.model.Mutation
import com.prac.feature.detail.view.UiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DetailReducerProcessorTest {

    private val detailReducerProcessorTest = DetailReducerProcessor()

    @Test
    fun 로딩상태_변환테스트() {
        // when
        val result = detailReducerProcessorTest.invoke(Mutation.ShowLoading, UiState.Loading)

        // then
        assertTrue(result is UiState.Loading)
    }

    @Test
    fun 에러상태_변환테스트() {
        // given
        val expectedErrorMessage = "test"

        // when
        val result = detailReducerProcessorTest.invoke(Mutation.ShowError(expectedErrorMessage), UiState.Loading)

        // then
        assertTrue(result is UiState.Error)
        assertEquals((result as UiState.Error).message, expectedErrorMessage)
    }

    @Test
    fun 레파지토리상태_변환테스트() {
        // given
        val repository = RepositoryDetail()

        // when
        val result = detailReducerProcessorTest.invoke(Mutation.ShowRepository(repository), UiState.Loading)

        // then
        assertEquals((result as UiState.Content).repository, repository)
    }
}