package com.example.courtgate.home.di

import android.content.Context
import androidx.room.Dao
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.courtgate.home.data.local.CourtDatabase
import com.example.courtgate.home.data.local.LastResultDAO
import com.example.courtgate.home.data.repository.HomeRepositoryImpl
import com.example.courtgate.home.domain.repository.HomeRepository
import com.example.courtgate.home.domain.usecase.GetLastResultUseCase
import com.example.courtgate.home.domain.usecase.HomeUsesCases
import com.example.courtgate.home.domain.usecase.InsertLastResultUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
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
    fun providesHomeUsesCases(homeRepository: HomeRepository): HomeUsesCases {
        return HomeUsesCases(
            insertLastResult = InsertLastResultUseCase(homeRepository),
            getLastResultUseCase = GetLastResultUseCase(homeRepository)
        )
    }

    @Singleton
    @Provides
    fun provideHomeRepository(dao: LastResultDAO): HomeRepository {
        return HomeRepositoryImpl(dao)
    }
}