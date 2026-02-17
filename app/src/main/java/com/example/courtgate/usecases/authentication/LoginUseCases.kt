package com.example.courtgate.usecases.authentication

data class LoginUseCases(
    val validatePasswordUseCase  : ValidatePasswordUseCase,
    val validateEmailUseCase: ValidateEmailUseCase,
    val loginWithEmailUseCase: LoginWithEmailUseCase,
)