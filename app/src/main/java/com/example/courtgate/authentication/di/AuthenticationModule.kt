package com.example.courtgate.authentication.di

import com.example.courtgate.authentication.data.AuthenticationRepositoryImpl
import com.example.courtgate.authentication.data.matcher.EmailMatcherImpl
import com.example.courtgate.authentication.domain.matcher.EmailMatcher
import com.example.courtgate.authentication.domain.repository.AuthenticationRepository
import com.example.courtgate.authentication.domain.usecase.GetUserIdUseCase
import com.example.courtgate.authentication.domain.usecase.LoginUseCases
import com.example.courtgate.authentication.domain.usecase.LoginWithEmailUseCase
import com.example.courtgate.authentication.domain.usecase.SignUpUseCases
import com.example.courtgate.authentication.domain.usecase.SignUpWhitEmailUseCase
import com.example.courtgate.authentication.domain.usecase.ValidateEmailUseCase
import com.example.courtgate.authentication.domain.usecase.ValidatePasswordUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthenticationModule {

    @Provides
    @Singleton
    fun providesAuthenticationRepository(): AuthenticationRepository {
        return AuthenticationRepositoryImpl()
    }

    @Provides
    @Singleton
    fun providesEmailMatcher(): EmailMatcher {
        return EmailMatcherImpl()
    }

    @Provides
    @Singleton
    fun providesLoginUseCases(
        repository: AuthenticationRepository,
        emailMatcher: EmailMatcher
    ): LoginUseCases {
        return LoginUseCases(
            validatePasswordUseCase = ValidatePasswordUseCase(),
            validateEmailUseCase = ValidateEmailUseCase(
                emailMatcher = emailMatcher
            ),
            loginWithEmailUseCase = LoginWithEmailUseCase(repository)
        )
    }

    @Provides
    @Singleton
    fun providesSignUpUseCases(
        repository: AuthenticationRepository,
        emailMatcher: EmailMatcher
    ): SignUpUseCases {
        return SignUpUseCases(
            validatePasswordUseCase = ValidatePasswordUseCase(),
            validateEmailUseCase = ValidateEmailUseCase(
                emailMatcher = emailMatcher
            ),
            signUpWhitEmailUseCase = SignUpWhitEmailUseCase(repository)
        )
    }

    @Provides
    @Singleton
    fun providesGetUserIdUseCase(
        repository: AuthenticationRepository
    ): GetUserIdUseCase {
        return GetUserIdUseCase(repository)
    }
}
