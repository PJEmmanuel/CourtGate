package com.example.courtgate.usecases.find

import com.example.courtgate.data.ManageCourtRepository
import com.example.courtgate.domain.models.FilterOption
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFilterOptionUseCase @Inject constructor(private val repository: ManageCourtRepository) {
    operator fun invoke(): Flow<List<FilterOption>> = repository.getFilterOption()

}
