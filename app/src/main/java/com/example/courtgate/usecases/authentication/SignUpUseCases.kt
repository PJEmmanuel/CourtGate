package com.example.courtgate.usecases.authentication

data class SignUpUseCases(
    val validatePasswordUseCase  : ValidatePasswordUseCase,
    val validateEmailUseCase: ValidateEmailUseCase,
    val signUpWhitEmailUseCase: SignUpWhitEmailUseCase,
)
