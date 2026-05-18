package com.example.courtgate.ui.presentation.booking

import com.example.courtgate.core.CoroutinesTestRule
import com.example.courtgate.usecases.booking.GetCourtSelectedByCodeUseCase
import com.example.courtgate.usecases.booking.GetFreeHoursOnReservedCourtsUseCase
import com.example.courtgate.usecases.booking.SetNewBookingUseCase
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class BookingViewModelTest {
    @get:Rule
    val coroutinesTestRule = CoroutinesTestRule()

    @Mock
    lateinit var getCourtSelectedByCodeUseCase: GetCourtSelectedByCodeUseCase

    @Mock
    lateinit var getFreeHoursOnReservedCourtsUseCase: GetFreeHoursOnReservedCourtsUseCase

    @Mock
    lateinit var setNewBookingUseCase: SetNewBookingUseCase

}