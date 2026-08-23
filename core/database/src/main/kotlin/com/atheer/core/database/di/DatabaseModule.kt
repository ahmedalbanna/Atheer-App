package com.atheer.core.database.di

import android.content.Context
import androidx.room.Room
import com.atheer.core.database.AtheerDatabase
import com.atheer.core.database.CommentDao
import com.atheer.core.database.VideoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providesDatabase(@ApplicationContext context: Context): AtheerDatabase =
        Room.databaseBuilder(context, AtheerDatabase::class.java, AtheerDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()

    @Provides
    fun providesVideoDao(database: AtheerDatabase): VideoDao = database.videoDao()

    @Provides
    fun providesCommentDao(database: AtheerDatabase): CommentDao = database.commentDao()
}
