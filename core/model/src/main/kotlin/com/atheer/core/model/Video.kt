package com.atheer.core.model

data class Video(
    val id: String,
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

data class Comment(
    val id: Long = 0L,
    val videoId: String,
    val author: String,
    val text: String,
    val timestamp: Long = 0L,
)
