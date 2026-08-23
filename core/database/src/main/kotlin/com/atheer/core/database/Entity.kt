package com.atheer.core.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.atheer.core.model.VideoCategory

@Entity(tableName = "videos")
data class VideoEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val videoUrl: String,
    val thumbnailUrl: String,
    val channelName: String,
    val channelAvatar: String,
    val views: Long = 0,
    val likes: Long = 0,
    val duration: String = "",
    val category: VideoCategory = VideoCategory.ENTERTAINMENT,
    val isShort: Boolean = false,
    val isSubscribed: Boolean = false,
    val isLiked: Boolean = false,
    val isDownloaded: Boolean = false,
    val isLive: Boolean = false,
    val liveViewerCount: Int = 0,
    val timestamp: Long = 0L,
)

@Entity(
    tableName = "comments",
    indices = [Index("videoId")],
)
data class CommentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val videoId: String,
    val author: String,
    val text: String,
    val timestamp: Long = 0L,
)
