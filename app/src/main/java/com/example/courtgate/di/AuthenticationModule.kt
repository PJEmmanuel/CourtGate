package com.example.courtgate.di

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
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
import com.example.courtgate.ui.navigation.screens.Booking
import com.example.courtgate.usecases.authentication.IsUserLoggedInUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import java.time.Clock
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthenticationModule {

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

// DataSources - ModuleDataSource
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

//Feature Booking - BookingArgsModule
@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class CourtCode

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class SelectedDay

@Module
@InstallIn(ViewModelComponent::class)
object BookingArgsModule {
    @Provides @ViewModelScoped
    @CourtCode
    fun provideCode(ssh: SavedStateHandle): String = ssh.toRoute<Booking>().code

    @Provides @ViewModelScoped @SelectedDay
    fun provideSelectedDay(ssh: SavedStateHandle): Long = ssh.toRoute<Booking>().date
}