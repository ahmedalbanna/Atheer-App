package com.atheer.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [VideoEntity::class, CommentEntity::class],
    version = 2,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class AtheerDatabase : RoomDatabase() {

    abstract fun videoDao(): VideoDao
    abstract fun commentDao(): CommentDao

    companion object {
        const val DATABASE_NAME = "atheer_database"
    }
}
