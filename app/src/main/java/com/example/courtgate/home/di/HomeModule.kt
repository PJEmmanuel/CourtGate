package com.example.courtgate.home.di

import android.content.Context
import androidx.room.Dao
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.courtgate.home.data.local.CourtDatabase
import com.example.courtgate.home.data.local.LastResultDAO
import com.example.courtgate.home.data.repository.HomeRepositoryImpl
import com.example.courtgate.home.domain.repository.HomeRepository
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

object HomeModule {

    @Singleton
    @Provides
    fun providesLastResultDAO(@ApplicationContext context: Context): LastResultDAO {
        return Room.databaseBuilder(
            context = context,
            CourtDatabase::class.java,
            "court_db"
        ).build().LastResultDAO()
    }

    @Singleton
    @Provides
    fun provideHomeRepository(dao: LastResultDAO): HomeRepository {
        return HomeRepositoryImpl(dao)
    }
}