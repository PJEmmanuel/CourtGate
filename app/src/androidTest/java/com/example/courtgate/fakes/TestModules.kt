package com.example.courtgate.fakes

import android.content.Context
import androidx.room.Room
import com.example.courtgate.data.datasources.AuthDataSource
import com.example.courtgate.data.datasources.CourtLocalDataSource
import com.example.courtgate.data.datasources.CourtRemoteDataSource
import com.example.courtgate.data.datasources.ResultLocalDataSource
import com.example.courtgate.data.datasources.SyncPreferencesDataSource
import com.example.courtgate.di.ModuleDataSource
import com.example.courtgate.di.RoomModule
import com.example.courtgate.framework.ManageCourtRoomDataSource
import com.example.courtgate.framework.MatchRoomDataSourceResult
import com.example.courtgate.framework.database.CourtDatabase
import com.example.courtgate.framework.database.LastResultDAO
import com.example.courtgate.framework.database.ManageCourtDAO
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

// Firebase (Auth y Firestore) y DataStore pasan a fakes en memoria.
// Room sigue siendo el real, pero en memoria (InMemoryRoomModule).
@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [ModuleDataSource::class])
abstract class FakeDataSourceModule {
    @Binds
    abstract fun bindAuthDS(fake: FakeAuthDataSource): AuthDataSource

    @Binds
    abstract fun bindCourtRemoteDS(fake: FakeCourtRemoteDataSource): CourtRemoteDataSource

    @Binds
    abstract fun bindSyncPreferencesDS(fake: FakeSyncPreferences): SyncPreferencesDataSource

    @Binds
    abstract fun bindMatchLocalDS(localDS: MatchRoomDataSourceResult): ResultLocalDataSource

    @Binds
    abstract fun bindCourtLocalDS(localDS: ManageCourtRoomDataSource): CourtLocalDataSource
}

// Base en memoria: los tests no tocan court_db (ni los resultados guardados en el dispositivo)
@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [RoomModule::class])
object InMemoryRoomModule {
    @Provides
    @Singleton
    fun providesDatabase(@ApplicationContext context: Context): CourtDatabase =
        Room.inMemoryDatabaseBuilder(context, CourtDatabase::class.java).build()

    @Provides
    fun provideLastResult(db: CourtDatabase): LastResultDAO = db.LastResultDAO()

    @Provides
    fun providesCourt(db: CourtDatabase): ManageCourtDAO = db.ManageCourtDAO()
}
