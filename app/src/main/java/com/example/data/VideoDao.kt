package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoDao {
    @Query("SELECT * FROM videos ORDER BY timestamp DESC")
    fun getAllVideos(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE isShort = 1 ORDER BY timestamp DESC")
    fun getShorts(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE isSubscribed = 1 ORDER BY timestamp DESC")
    fun getSubscribedVideos(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE isDownloaded = 1 ORDER BY timestamp DESC")
    fun getDownloadedVideos(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE id = :id LIMIT 1")
    suspend fun getVideoById(id: String): VideoEntity?

    @Query("SELECT * FROM videos WHERE id = :id")
    fun observeVideoById(id: String): Flow<VideoEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideos(videos: List<VideoEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: VideoEntity)

    @Update
    suspend fun updateVideo(video: VideoEntity)

    @Query("UPDATE videos SET isLiked = :isLiked, likes = likes + :likeOffset WHERE id = :id")
    suspend fun toggleLike(id: String, isLiked: Boolean, likeOffset: Int)

    @Query("UPDATE videos SET isSubscribed = :isSubscribed WHERE id = :id")
    suspend fun toggleSubscription(id: String, isSubscribed: Boolean)

    @Query("UPDATE videos SET isSubscribed = :isSubscribed WHERE channelName = :channelName")
    suspend fun updateSubscriptionByChannel(channelName: String, isSubscribed: Boolean)

    @Query("UPDATE videos SET isDownloaded = :isDownloaded WHERE id = :id")
    suspend fun updateDownloadStatus(id: String, isDownloaded: Boolean)

    @Query("UPDATE videos SET views = views + 1 WHERE id = :id")
    suspend fun incrementViews(id: String)

    // Comments queries
    @Query("SELECT * FROM comments WHERE videoId = :videoId ORDER BY timestamp DESC")
    fun getCommentsForVideo(videoId: String): Flow<List<CommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)
}
