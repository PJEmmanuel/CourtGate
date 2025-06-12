package com.example.courtgate.authentication.domain.usecase

data class LoginUseCases(
    val validatePasswordUseCase  : ValidatePasswordUseCase,
    val validateEmailUseCase: ValidateEmailUseCase,
    val loginWithEmailUseCase: LoginWithEmailUseCase,
)
