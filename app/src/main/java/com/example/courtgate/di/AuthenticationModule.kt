package com.example.courtgate.di

import com.example.courtgate.core.matcher.EmailMatcherImpl
import com.example.courtgate.core.matcher.EmailMatcher
import com.example.courtgate.data.AuthenticationRepository
import com.example.courtgate.data.AuthenticationRepositoryImpl
import com.example.courtgate.usecases.authentication.GetUserIdUseCase
import com.example.courtgate.usecases.authentication.LoginUseCases
import com.example.courtgate.usecases.authentication.LoginWithEmailUseCase
import com.example.courtgate.usecases.authentication.SignUpUseCases
import com.example.courtgate.usecases.authentication.SignUpWhitEmailUseCase
import com.example.courtgate.usecases.authentication.ValidateEmailUseCase
import com.example.courtgate.usecases.authentication.ValidatePasswordUseCase
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