package com.example.courtgate.authentication.domain.usecase

data class SignUpUseCases(
    val validatePasswordUseCase  : ValidatePasswordUseCase,
    val validateEmailUseCase: ValidateEmailUseCase,
    val signUpWhitEmailUseCase: SignUpWhitEmailUseCase,
)
