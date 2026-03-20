package com.example.courtgate.di

import android.content.Context
import androidx.room.Room
import com.example.courtgate.framework.database.CourtDatabase
import com.example.courtgate.framework.database.LastResultDAO
import com.example.courtgate.framework.database.ManageCourtDAO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Singleton
    @Provides
    fun providesDatabase(@ApplicationContext context: Context): CourtDatabase {
        return Room.databaseBuilder(
            context = context,
            CourtDatabase::class.java,
            "court_db"
        )
            .fallbackToDestructiveMigration() //TODO Fallback activo
            .build()
    }

    @Provides
    fun provideLastResult(courtDataBase: CourtDatabase): LastResultDAO {
        return courtDataBase.LastResultDAO()
    }

    @Provides
    fun providesCourt(courtDatabase: CourtDatabase): ManageCourtDAO{
        return courtDatabase.ManageCourtDAO()
    }
}