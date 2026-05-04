package com.example.courtgate.ui.presentation.find

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.courtgate.ResultCourt
import com.example.courtgate.domain.models.Court
import com.example.courtgate.domain.models.FilterOption
import java.time.ZonedDateTime

class FindStateScreen(
    private val state: ResultCourt<FindState>,
    val onSelectedDate: (ZonedDateTime) -> Unit,
) {
    val dateSevenDaysAhead: Int = 7
    val mainDate: ZonedDateTime = ZonedDateTime.now()
    val datesRange: List<ZonedDateTime> = (0 until dateSevenDaysAhead).map {
        mainDate.plusDays(it.toLong())
    }

    val stateIsSuccess: FindState?
        get() = (state as? ResultCourt.Success)?.data

    val selectedDate: ZonedDateTime
        get() = stateIsSuccess?.selectedDate ?: mainDate
}

@Composable
fun rememberFindStateScreen(
    state: ResultCourt<FindState>,
    onSelectedDate: (ZonedDateTime) -> Unit,
): FindStateScreen = remember(state, onSelectedDate) {
    FindStateScreen(state, onSelectedDate)
}

data class FindState(
    val courts: List<Court> = emptyList(),
    val filterList: List<FilterOption> = emptyList(),
    val selectedDate: ZonedDateTime,
)
