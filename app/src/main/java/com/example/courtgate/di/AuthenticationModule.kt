package com.example.courtgate.di

import com.example.courtgate.data.AuthenticationRepository
import com.example.courtgate.data.datasources.AuthDataSource
import com.example.courtgate.data.datasources.CourtLocalDataSource
import com.example.courtgate.data.datasources.CourtRemoteDataSource
import com.example.courtgate.data.datasources.ResultLocalDataSource
import com.example.courtgate.data.datasources.SyncPreferencesDataSource
import com.example.courtgate.framework.FirebaseAuthDataSource
import com.example.courtgate.framework.FirebaseFirestoreDataSource
import com.example.courtgate.framework.ManageCourtRoomDataSource
import com.example.courtgate.framework.MatchRoomDataSourceResult
import com.example.courtgate.framework.DataStoreSyncPreferencesDataSource
import com.example.courtgate.usecases.authentication.IsUserLoggedInUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import java.time.Clock
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthenticationModule {

    //TODO: Separar otro modulo para Firebase?
    @Provides
    @Singleton
    fun providesFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun providesFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun providesGetUserIdUseCase(
        repository: AuthenticationRepository
    ): IsUserLoggedInUseCase {
        return IsUserLoggedInUseCase(repository)
    }

    //para zona de GetAllCourtToShowUseCase
    @Provides
    @Singleton
    fun providesClock(): Clock = Clock.systemDefaultZone()

    @Provides
    @Singleton
    fun providesIODispatcher(): CoroutineDispatcher = Dispatchers.IO
}

@Module
@InstallIn(SingletonComponent::class)
abstract class ModuleDataSource {
    @Binds
    abstract fun bindAuthDS(authDS: FirebaseAuthDataSource): AuthDataSource

    @Binds
    abstract fun bindMatchLocalDS(localDS: MatchRoomDataSourceResult): ResultLocalDataSource

    @Binds
    abstract fun bindCourtRemoteDS(remoteDS: FirebaseFirestoreDataSource): CourtRemoteDataSource

    @Binds
    abstract fun bindCourtLocalDS(localDS: ManageCourtRoomDataSource): CourtLocalDataSource

    @Binds
    abstract fun bindSyncPreferencesDS(syncPrefDS: DataStoreSyncPreferencesDataSource): SyncPreferencesDataSource
}