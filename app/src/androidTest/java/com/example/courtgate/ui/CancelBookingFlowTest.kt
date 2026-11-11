package com.example.courtgate.ui

import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ActivityScenario
import com.example.courtgate.MainActivity
import com.example.courtgate.R
import com.example.courtgate.domain.models.CourtBooking
import com.example.courtgate.fakes.FakeAuthDataSource
import com.example.courtgate.fakes.FakeAuthDataSource.Companion.TEST_USER
import com.example.courtgate.fakes.FakeCourtRemoteDataSource
import com.example.courtgate.fakes.FakeCourtRemoteDataSource.Companion.TEST_COURT
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltAndroidTest
class CancelBookingFlowTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createEmptyComposeRule()

    @Inject
    lateinit var auth: FakeAuthDataSource

    @Inject
    lateinit var remote: FakeCourtRemoteDataSource

    private val booking = CourtBooking(
        id = "booking-1",
        code = TEST_COURT.code,
        date = LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant(),
        hour = "18:00",
        userId = TEST_USER.uid
    )

    @Before
    fun setUp() {
        hiltRule.inject()
        auth.logInAsTestUser()
        remote.bookings.value = listOf(booking)
        ActivityScenario.launch(MainActivity::class.java)
    }

    @Test
    fun cancelBooking_deletesItAndConfirms() {
        composeRule.waitForText(str(R.string.nav_booking))
        composeRule.onNodeWithText(str(R.string.nav_booking)).performClick()

        composeRule.waitForText(booking.code)
        composeRule.onNodeWithText(booking.code).performClick()

        composeRule.waitForText(str(R.string.my_booking_cancel_title))
        composeRule.onNodeWithText(str(R.string.my_booking_action_cancel_booking)).performClick()
        composeRule.waitForText(str(R.string.my_booking_succeeded_title))

        assertEquals(listOf(booking.id), remote.deletedBookingIds)
        assertTrue(remote.bookings.value.isEmpty())
    }
}
