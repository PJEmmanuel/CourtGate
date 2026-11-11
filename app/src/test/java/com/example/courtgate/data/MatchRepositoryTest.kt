package com.example.courtgate.data

import com.example.courtgate.ResultManage
import com.example.courtgate.core.lastResultTest
import com.example.courtgate.data.datasources.ResultLocalDataSource
import com.example.courtgate.domain.models.DomainError
import com.example.courtgate.domain.models.LastResult
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class MatchRepositoryTest {

    @Mock
    lateinit var resultLocalDataSource: ResultLocalDataSource

    private lateinit var repository: MatchRepository

    private val lastResult1 = lastResultTest(0, 6, 0, 1, 6, 6, 4)

    @Before
    fun setUp() {
        repository = MatchRepository(resultLocalDataSource)
    }

    @Test
    fun `If room not empty, get lastResult`(): Unit = runTest {
        val listLastResult = listOf(lastResult1)
        whenever(resultLocalDataSource.getLastResult).thenReturn(flowOf(listLastResult))

        val result = repository.lastResult.first()

        assertEquals(listLastResult, result)
    }

    @Test
    fun `If database empty, get empty lastResult`(): Unit = runTest {
        val lastResult = emptyList<LastResult>()
        whenever(resultLocalDataSource.getLastResult).thenReturn(flowOf(lastResult))

        val result = repository.lastResult.first()

        assertEquals(lastResult, result)
    }

    @Test
    fun `If database emits nulls, they are filtered out`(): Unit = runTest {
        val expected = listOf(lastResult1)
        whenever(resultLocalDataSource.getLastResult).thenReturn(flowOf(listOf(lastResult1, null)))

        val result = repository.lastResult.first()

        assertEquals(expected, result)
    }

    @Test
    fun `Insert lastResult in database`(): Unit = runTest {
        whenever(resultLocalDataSource.insertLastResult(lastResult1)).thenReturn(ResultManage.Success(Unit))

        val result = repository.insertLastResult(lastResult1)

        assertEquals(ResultManage.Success(Unit), result)
        verify(resultLocalDataSource).insertLastResult(lastResult1)
    }

    @Test
    fun `Insert returns the local error when Room fails`(): Unit = runTest {
        whenever(resultLocalDataSource.insertLastResult(lastResult1))
            .thenReturn(ResultManage.Failure(DomainError.Local.DiskFull))

        val result = repository.insertLastResult(lastResult1)

        assertEquals(ResultManage.Failure(DomainError.Local.DiskFull), result)
    }

    @Test
    fun `Delete a result of database`():Unit = runTest {
        whenever(resultLocalDataSource.deleteLastResult(lastResult1)).thenReturn(ResultManage.Success(Unit))

        val result = repository.deleteLastResult(lastResult1)

        assertEquals(ResultManage.Success(Unit), result)
        verify(resultLocalDataSource).deleteLastResult(lastResult1)
    }

    @Test
    fun `Edit a result of database`():Unit=runTest {
        whenever(resultLocalDataSource.editLastResult(lastResult1)).thenReturn(ResultManage.Success(Unit))

        val result = repository.editLastResult(lastResult1)

        assertEquals(ResultManage.Success(Unit), result)
        verify(resultLocalDataSource).editLastResult(lastResult1)
    }

    @Test
    fun `Delete all results of database`(): Unit = runTest {
        whenever(resultLocalDataSource.deleteAllResults()).thenReturn(ResultManage.Success(Unit))

        val result = repository.deleteAllResults()

        assertEquals(ResultManage.Success(Unit), result)
        verify(resultLocalDataSource).deleteAllResults()
    }
}
