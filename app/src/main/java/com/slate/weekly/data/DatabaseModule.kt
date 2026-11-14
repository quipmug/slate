package com.slate.weekly.data

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.installIn
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SlateDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            SlateDatabase::class.java,
            "slate_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideHabitDao(database: SlateDatabase): HabitDao {
        return database.habitDao()
    }
}
