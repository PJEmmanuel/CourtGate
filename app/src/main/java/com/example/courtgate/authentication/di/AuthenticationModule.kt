package com.example.courtgate.authentication.di

import com.example.courtgate.authentication.data.AuthenticationRepositoryImpl
import com.example.courtgate.authentication.domain.AuthenticationRepository
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
}
