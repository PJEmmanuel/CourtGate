package com.example.courtgate.usecases.find

import com.example.courtgate.ResultManage
import com.example.courtgate.data.ManageCourtRepository
import com.example.courtgate.domain.models.DomainError
import com.example.courtgate.domain.models.FilterOption
import javax.inject.Inject

class GetFilterOptionUseCase @Inject constructor(private val repository: ManageCourtRepository) {
    suspend operator fun invoke(): ResultManage<List<FilterOption>, DomainError> = repository.getFilterOption()

}
