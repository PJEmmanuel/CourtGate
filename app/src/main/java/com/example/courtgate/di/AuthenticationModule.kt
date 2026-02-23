package com.example.courtgate.di

import com.example.courtgate.data.AuthenticationRepository
import com.example.courtgate.data.datasources.AuthDataSource
import com.example.courtgate.data.datasources.LocalDataSource
import com.example.courtgate.framework.FirebaseAuthDataSource
import com.example.courtgate.framework.MatchRoomDataSource
import com.example.courtgate.usecases.authentication.IsUserLoggedInUseCase
import com.google.firebase.auth.FirebaseAuth
import dagger.Binds
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
    fun providesFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun providesGetUserIdUseCase(
        repository: AuthenticationRepository
    ): IsUserLoggedInUseCase {
        return IsUserLoggedInUseCase(repository)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class ModuleDataSource {
    @Binds
    abstract fun bindAuthDS(authDS: FirebaseAuthDataSource): AuthDataSource

    @Binds
    abstract fun bindLocalDS(localDS: MatchRoomDataSource): LocalDataSource
}