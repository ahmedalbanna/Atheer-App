package com.atheer.core.data

import com.atheer.core.model.Comment
import com.atheer.core.model.Video
import com.atheer.core.model.VideoCategory
import kotlinx.coroutines.flow.Flow

interface VideoRepository {

    fun observeAllVideos(): Flow<List<Video>>

    fun observeShorts(): Flow<List<Video>>

    fun observeSubscribedVideos(): Flow<List<Video>>

    fun observeDownloadedVideos(): Flow<List<Video>>

    fun observeVideo(id: String): Flow<Video?>

    fun observeComments(videoId: String): Flow<List<Comment>>

    suspend fun getVideo(id: String): Video?

    suspend fun setLiked(videoId: String, isLiked: Boolean)

    suspend fun setSubscribed(videoId: String, isSubscribed: Boolean)

    suspend fun setDownloaded(videoId: String, isDownloaded: Boolean)

    suspend fun incrementViews(videoId: String)

    suspend fun addComment(videoId: String, author: String, text: String)

    suspend fun uploadVideo(
        title: String,
        description: String,
        category: VideoCategory,
        isShort: Boolean,
        videoUrl: String?,
        thumbnailUrl: String?,
    )
}
