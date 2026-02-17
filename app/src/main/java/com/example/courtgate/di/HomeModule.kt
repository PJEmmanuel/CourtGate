package com.example.courtgate.di

import android.content.Context
import androidx.room.Room
import com.example.courtgate.data.HomeRepository
import com.example.courtgate.framework.database.CourtDatabase
import com.example.courtgate.framework.database.LastResultDAO
import com.example.courtgate.usecases.booking.BookingUseCases
import com.example.courtgate.usecases.booking.GetCourtSelectedByCode
import com.example.courtgate.usecases.booking.GetFreeHoursOnReservedCourts
import com.example.courtgate.usecases.find.FindUseCases
import com.example.courtgate.usecases.find.GetAllCourtToShowUseCase
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
    fun provideHomeRepository(dao: LastResultDAO): HomeRepository {
        return HomeRepository(dao)
    }

    @Singleton
    @Provides
    fun providesFindUseCases(
        repository: HomeRepository,
    ): FindUseCases {
        return FindUseCases(
            getAllCourtToShowUseCase = GetAllCourtToShowUseCase(repository)
        )
    }

    @Singleton
    @Provides
    fun providesBookingUseCases(repository: HomeRepository): BookingUseCases {
        return BookingUseCases(
            getCourtSelectedByCode = GetCourtSelectedByCode(repository),
            getFreeHoursOnReservedCourts = GetFreeHoursOnReservedCourts(repository)
        )
    }
}