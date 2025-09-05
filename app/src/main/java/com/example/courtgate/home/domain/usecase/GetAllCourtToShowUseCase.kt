package com.example.courtgate.home.domain.usecase

import com.example.courtgate.home.domain.models.CourtList
import com.example.courtgate.home.domain.repository.HomeRepository

class GetAllCourtToShowUseCase(private val repository: HomeRepository) {
    suspend operator fun invoke(): Result<List<CourtList>> {
        return repository.getAllCourtToShow()

    }
}

/*class GetAllCourtToShowUseCase(private val repository: HomeRepository) {
    suspend operator fun invoke(location: String? = null): Result<List<CourtList>> {
        val result = repository.getAllCourtToShow()

        return result.mapCatching { courts ->
            if (location.isNullOrEmpty()) {
                courts
            } else {
                courts.filter { it.located == location }
            }
        }
    }
}*/