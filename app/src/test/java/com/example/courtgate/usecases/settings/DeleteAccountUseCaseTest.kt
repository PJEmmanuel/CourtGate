package com.example.courtgate.usecases.settings

import com.example.courtgate.ResultManage
import com.example.courtgate.data.AuthenticationRepository
import com.example.courtgate.data.ManageCourtRepository
import com.example.courtgate.data.MatchRepository
import com.example.courtgate.domain.models.DomainError
import com.example.courtgate.domain.models.DomainException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class DeleteAccountUseCaseTest {

    @Mock
    lateinit var authRepository: AuthenticationRepository

    @Mock
    lateinit var courtRepository: ManageCourtRepository

    @Mock
    lateinit var matchRepository: MatchRepository

    private lateinit var useCase: DeleteAccountUseCase

    private val userId = "user_1"
    private val password = "asdASD123"

    @Before
    fun setUp() {
        useCase = DeleteAccountUseCase(authRepository, courtRepository, matchRepository)
    }

    private suspend fun stubHappyPath() {
        whenever(authRepository.getCurrentUserId()).thenReturn(userId)
        whenever(authRepository.reauthenticate(password)).thenReturn(Result.success(Unit))
        whenever(courtRepository.deleteAllUserBookings(userId)).thenReturn(ResultManage.Success(Unit))
        whenever(matchRepository.deleteAllResults()).thenReturn(ResultManage.Success(Unit))
        whenever(authRepository.deleteCurrentUser()).thenReturn(Result.success(Unit))
    }

    @Test
    fun `deletes bookings, local results and then the account`(): Unit = runTest {
        stubHappyPath()

        val result = useCase(password)

        assertEquals(ResultManage.Success(Unit), result)
        inOrder(authRepository, courtRepository, matchRepository) {
            verify(authRepository).reauthenticate(password)
            verify(courtRepository).deleteAllUserBookings(userId)
            verify(matchRepository).deleteAllResults()
            verify(authRepository).deleteCurrentUser()
        }
    }

    @Test
    fun `without session returns SessionExpired and deletes nothing`(): Unit = runTest {
        whenever(authRepository.getCurrentUserId()).thenReturn(null)

        val result = useCase(password)

        assertEquals(ResultManage.Failure(DomainError.Auth.SessionExpired), result)
        verify(courtRepository, never()).deleteAllUserBookings(any())
        verify(matchRepository, never()).deleteAllResults()
        verify(authRepository, never()).deleteCurrentUser()
    }

    @Test
    fun `wrong password returns InvalidCredentials and deletes nothing`(): Unit = runTest {
        whenever(authRepository.getCurrentUserId()).thenReturn(userId)
        whenever(authRepository.reauthenticate(password))
            .thenReturn(Result.failure(DomainException(DomainError.Auth.InvalidCredentials)))

        val result = useCase(password)

        assertEquals(ResultManage.Failure(DomainError.Auth.InvalidCredentials), result)
        verify(courtRepository, never()).deleteAllUserBookings(any())
        verify(matchRepository, never()).deleteAllResults()
        verify(authRepository, never()).deleteCurrentUser()
    }

    @Test
    fun `if bookings cannot be deleted the account is kept`(): Unit = runTest {
        stubHappyPath()
        whenever(courtRepository.deleteAllUserBookings(userId))
            .thenReturn(ResultManage.Failure(DomainError.Remote.ServerError))

        val result = useCase(password)

        assertEquals(ResultManage.Failure(DomainError.Remote.ServerError), result)
        verify(matchRepository, never()).deleteAllResults()
        verify(authRepository, never()).deleteCurrentUser()
    }

    @Test
    fun `if local results cannot be deleted the account is kept`(): Unit = runTest {
        stubHappyPath()
        whenever(matchRepository.deleteAllResults())
            .thenReturn(ResultManage.Failure(DomainError.Local.UnknownLocalError))

        val result = useCase(password)

        assertEquals(ResultManage.Failure(DomainError.Local.UnknownLocalError), result)
        verify(authRepository, never()).deleteCurrentUser()
    }

    @Test
    fun `account deletion failure is returned`(): Unit = runTest {
        stubHappyPath()
        whenever(authRepository.deleteCurrentUser())
            .thenReturn(Result.failure(DomainException(DomainError.Auth.RecentLoginRequired)))

        val result = useCase(password)

        assertEquals(ResultManage.Failure(DomainError.Auth.RecentLoginRequired), result)
    }

    @Test
    fun `unexpected auth exceptions map to UnknownAuthError`(): Unit = runTest {
        stubHappyPath()
        whenever(authRepository.deleteCurrentUser()).thenReturn(Result.failure(RuntimeException()))

        val result = useCase(password)

        assertEquals(ResultManage.Failure(DomainError.Auth.UnknownAuthError), result)
    }
}
