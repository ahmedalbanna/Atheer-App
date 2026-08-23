package com.atheer.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoDao {

    @Query("SELECT * FROM videos ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE isShort = 1 ORDER BY timestamp DESC")
    fun observeShorts(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE isSubscribed = 1 ORDER BY timestamp DESC")
    fun observeSubscribed(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE isDownloaded = 1 ORDER BY timestamp DESC")
    fun observeDownloaded(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): VideoEntity?

    @Query("SELECT * FROM videos WHERE id = :id LIMIT 1")
    fun observeById(id: String): Flow<VideoEntity?>

    @Query("SELECT COUNT(*) FROM videos")
    suspend fun count(): Int

    @Upsert
    suspend fun upsert(video: VideoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(videos: List<VideoEntity>)

    @Query("UPDATE videos SET isLiked = :isLiked, likes = MAX(0, likes + CASE WHEN :isLiked THEN +1 ELSE -1 END) WHERE id = :id")
    suspend fun setLiked(id: String, isLiked: Boolean)

    @Query("UPDATE videos SET isSubscribed = :isSubscribed WHERE channelName = :channelName")
    suspend fun setChannelSubscription(channelName: String, isSubscribed: Boolean)

    @Query("UPDATE videos SET isDownloaded = :isDownloaded WHERE id = :id")
    suspend fun setDownloaded(id: String, isDownloaded: Boolean)

    @Query("UPDATE videos SET views = views + 1 WHERE id = :id")
    suspend fun incrementViews(id: String)
}

@Dao
interface CommentDao {

    @Query("SELECT * FROM comments WHERE videoId = :videoId ORDER BY timestamp ASC")
    fun observeForVideo(videoId: String): Flow<List<CommentEntity>>

    @Query("SELECT COUNT(*) FROM comments")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(comment: CommentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(comments: List<CommentEntity>)
}
