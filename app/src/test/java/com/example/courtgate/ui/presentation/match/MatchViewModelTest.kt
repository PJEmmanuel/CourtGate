package com.example.courtgate.ui.presentation.match

import app.cash.turbine.test
import com.example.courtgate.ResultCourt
import com.example.courtgate.ResultManage
import com.example.courtgate.core.CoroutinesTestRule
import com.example.courtgate.core.lastResultTest
import com.example.courtgate.domain.models.DomainError
import com.example.courtgate.domain.models.DomainException
import com.example.courtgate.usecases.home.DeleteLastResultUseCase
import com.example.courtgate.usecases.home.EditLastResultUseCase
import com.example.courtgate.usecases.home.GetLastResultUseCase
import com.example.courtgate.usecases.home.InsertLastResultUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doSuspendableAnswer
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class MatchViewModelTest {
    @get:Rule
    val coroutinesTestRule = CoroutinesTestRule()

    @Mock
    lateinit var getLastResultUseCase: GetLastResultUseCase

    @Mock
    lateinit var insertLastResultUseCase: InsertLastResultUseCase

    @Mock
    lateinit var editLastResultUseCase: EditLastResultUseCase

    @Mock
    lateinit var deleteLastResultUseCase: DeleteLastResultUseCase

    private lateinit var vm: MatchViewModel

    private val savedResult = lastResultTest(7, 6, 3, 4, 6, 7, 5)
    private val results = listOf(savedResult)

    private fun stubResults() {
        whenever(getLastResultUseCase.invoke()).thenReturn(flowOf(results))
    }

    private fun buildVm() = MatchViewModel(
        getLastResultUseCase = getLastResultUseCase,
        insertLastResultUseCase = insertLastResultUseCase,
        editLastResultUseCase = editLastResultUseCase,
        deleteLastResultUseCase = deleteLastResultUseCase
    )

    // Rellena el formulario abierto con un 6-4 / 6-2
    private fun MatchViewModel.fillStraightSetsWin() {
        onScoreChange(0, Team.First, "6")
        onScoreChange(0, Team.Second, "4")
        onScoreChange(1, Team.First, "6")
        onScoreChange(1, Team.Second, "2")
    }

    private val straightSetsWin = lastResultTest(0, 6, 4, 6, 2, 0, 0)

    @Test
    fun `state emits Loading and then Success with the saved results`(): Unit = runTest {
        stubResults()
        vm = buildVm()

        vm.state.test {
            assertEquals(ResultCourt.Loading, awaitItem())
            assertEquals(ResultCourt.Success(MatchState(results = results)), awaitItem())
        }
    }

    @Test
    fun `state emits Error when Room fails`(): Unit = runTest {
        whenever(getLastResultUseCase.invoke())
            .thenReturn(flow { throw DomainException(DomainError.Local.DatabaseCorrupted) })
        vm = buildVm()

        vm.state.test {
            assertEquals(ResultCourt.Loading, awaitItem())
            assertEquals(ResultCourt.Error(DomainError.Local.DatabaseCorrupted), awaitItem())
        }
    }

    @Test
    fun `unexpected exceptions map to UnknownLocalError`(): Unit = runTest {
        whenever(getLastResultUseCase.invoke()).thenReturn(flow { throw RuntimeException("Room") })
        vm = buildVm()

        vm.state.test {
            assertEquals(ResultCourt.Loading, awaitItem())
            assertEquals(ResultCourt.Error(DomainError.Local.UnknownLocalError), awaitItem())
        }
    }

    @Test
    fun `onRetry after error loads results again and can succeed`(): Unit = runTest {
        whenever(getLastResultUseCase.invoke())
            .thenReturn(flow { throw RuntimeException("Room") })
            .thenReturn(flowOf(results))
        vm = buildVm()

        vm.state.test {
            assertEquals(ResultCourt.Loading, awaitItem())
            assertEquals(ResultCourt.Error(DomainError.Local.UnknownLocalError, 1), awaitItem())

            vm.onRetry()
            assertEquals(ResultCourt.Loading, awaitItem())
            assertEquals(ResultCourt.Success(MatchState(results = results)), awaitItem())
        }
    }

    @Test
    fun `onAddClicked opens an empty form`(): Unit = runTest {
        stubResults()
        vm = buildVm()

        vm.state.test {
            skipItems(2)
            vm.onAddClicked()
            runCurrent()

            val state = expectMostRecentItem() as ResultCourt.Success
            assertEquals(MatchSheetState.Editing(ResultForm()), state.data.sheet)
        }
    }

    @Test
    fun `onEditClicked opens the form filled with the result`(): Unit = runTest {
        stubResults()
        vm = buildVm()

        vm.state.test {
            skipItems(2)
            vm.onEditClicked(savedResult)
            runCurrent()

            val sheet = (expectMostRecentItem() as ResultCourt.Success).data.sheet
            assertEquals(MatchSheetState.Editing(savedResult.toForm()), sheet)
        }
    }

    @Test
    fun `onScoreChange keeps only digits and two characters`(): Unit = runTest {
        stubResults()
        vm = buildVm()

        vm.state.test {
            skipItems(2)
            vm.onAddClicked()
            vm.onScoreChange(0, Team.First, "6a-")
            vm.onScoreChange(0, Team.Second, "1234")
            runCurrent()

            val sheet = (expectMostRecentItem() as ResultCourt.Success).data.sheet
            val firstSet = (sheet as MatchSheetState.Editing).form.sets[0]
            assertEquals(SetScoreInput("6", "12"), firstSet)
        }
    }

    @Test
    fun `onSave with an invalid form shows the error and does not save`(): Unit = runTest {
        stubResults()
        vm = buildVm()

        vm.state.test {
            skipItems(2)
            vm.onAddClicked()
            vm.onScoreChange(0, Team.First, "6")
            vm.onSave()
            runCurrent()

            val sheet = (expectMostRecentItem() as ResultCourt.Success).data.sheet
            assertEquals(ResultFormError.MissingSets, (sheet as MatchSheetState.Editing).formError)
            verify(insertLastResultUseCase, never()).invoke(any())
        }
    }

    @Test
    fun `editing a score clears the form error`(): Unit = runTest {
        stubResults()
        vm = buildVm()

        vm.state.test {
            skipItems(2)
            vm.onAddClicked()
            vm.onSave()
            vm.onScoreChange(0, Team.First, "6")
            runCurrent()

            val sheet = (expectMostRecentItem() as ResultCourt.Success).data.sheet
            assertEquals(null, (sheet as MatchSheetState.Editing).formError)
        }
    }

    @Test
    fun `onSave inserts a new result and closes the form`(): Unit = runTest {
        stubResults()
        whenever(insertLastResultUseCase.invoke(any())).doReturn(ResultManage.Success(Unit))
        vm = buildVm()

        vm.state.test {
            skipItems(2)
            vm.onAddClicked()
            vm.fillStraightSetsWin()
            vm.onSave()
            runCurrent()

            val sheet = (expectMostRecentItem() as ResultCourt.Success).data.sheet
            assertEquals(MatchSheetState.Hidden, sheet)
            verify(insertLastResultUseCase).invoke(straightSetsWin)
            verify(editLastResultUseCase, never()).invoke(any())
        }
    }

    @Test
    fun `onSave edits an existing result keeping its id`(): Unit = runTest {
        stubResults()
        whenever(editLastResultUseCase.invoke(any())).doReturn(ResultManage.Success(Unit))
        vm = buildVm()

        vm.state.test {
            skipItems(2)
            vm.onEditClicked(savedResult)
            // 6-3 / 4-6 / 7-5 → 6-3 / 4-6 / 7-6
            vm.onScoreChange(2, Team.Second, "6")
            vm.onSave()
            runCurrent()

            val sheet = (expectMostRecentItem() as ResultCourt.Success).data.sheet
            assertEquals(MatchSheetState.Hidden, sheet)
            verify(editLastResultUseCase).invoke(savedResult.copy(player2Set3 = 6))
            verify(insertLastResultUseCase, never()).invoke(any())
        }
    }

    @Test
    fun `onSave keeps the form open with the error when Room fails`(): Unit = runTest {
        stubResults()
        whenever(insertLastResultUseCase.invoke(any()))
            .doReturn(ResultManage.Failure(DomainError.Local.DiskFull))
        vm = buildVm()

        vm.state.test {
            skipItems(2)
            vm.onAddClicked()
            vm.fillStraightSetsWin()
            vm.onSave()
            runCurrent()

            val sheet = (expectMostRecentItem() as ResultCourt.Success).data.sheet as MatchSheetState.Editing
            assertEquals(DomainError.Local.DiskFull, sheet.saveError)
            assertEquals(false, sheet.isSaving)
            assertEquals("6", sheet.form.sets[0].player1)
        }
    }

    @Test
    fun `onDismissSheet is ignored while saving`(): Unit = runTest {
        stubResults()
        whenever(insertLastResultUseCase.invoke(any())).doSuspendableAnswer { awaitCancellation() }
        vm = buildVm()

        vm.state.test {
            skipItems(2)
            vm.onAddClicked()
            vm.fillStraightSetsWin()
            vm.onSave()
            runCurrent()
            vm.onDismissSheet()
            runCurrent()

            val sheet = (expectMostRecentItem() as ResultCourt.Success).data.sheet
            assertEquals(true, (sheet as MatchSheetState.Editing).isSaving)
        }
    }

    @Test
    fun `onDeleteClicked asks for confirmation before deleting`(): Unit = runTest {
        stubResults()
        vm = buildVm()

        vm.state.test {
            skipItems(2)
            vm.onDeleteClicked(savedResult)
            runCurrent()

            val sheet = (expectMostRecentItem() as ResultCourt.Success).data.sheet
            assertEquals(MatchSheetState.ConfirmingDelete(savedResult), sheet)
            verify(deleteLastResultUseCase, never()).invoke(any())
        }
    }

    @Test
    fun `onConfirmDelete deletes the result and closes the dialog`(): Unit = runTest {
        stubResults()
        whenever(deleteLastResultUseCase.invoke(any())).doReturn(ResultManage.Success(Unit))
        vm = buildVm()

        vm.state.test {
            skipItems(2)
            vm.onDeleteClicked(savedResult)
            vm.onConfirmDelete()
            runCurrent()

            val sheet = (expectMostRecentItem() as ResultCourt.Success).data.sheet
            assertEquals(MatchSheetState.Hidden, sheet)
            verify(deleteLastResultUseCase).invoke(savedResult)
        }
    }

    @Test
    fun `onConfirmDelete shows the error when Room fails`(): Unit = runTest {
        stubResults()
        whenever(deleteLastResultUseCase.invoke(any()))
            .doReturn(ResultManage.Failure(DomainError.Local.UnknownLocalError))
        vm = buildVm()

        vm.state.test {
            skipItems(2)
            vm.onDeleteClicked(savedResult)
            vm.onConfirmDelete()
            runCurrent()

            val sheet = (expectMostRecentItem() as ResultCourt.Success).data.sheet
            assertEquals(
                MatchSheetState.ConfirmingDelete(savedResult, error = DomainError.Local.UnknownLocalError),
                sheet
            )
        }
    }

    @Test
    fun `onDismissSheet closes the delete dialog`(): Unit = runTest {
        stubResults()
        vm = buildVm()

        vm.state.test {
            skipItems(2)
            vm.onDeleteClicked(savedResult)
            runCurrent()
            vm.onDismissSheet()
            runCurrent()

            val sheet = (expectMostRecentItem() as ResultCourt.Success).data.sheet
            assertEquals(MatchSheetState.Hidden, sheet)
        }
    }
}
