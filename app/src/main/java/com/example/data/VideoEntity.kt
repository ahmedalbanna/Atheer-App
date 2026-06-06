package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "videos")
data class VideoEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val videoUrl: String,
    val thumbnailUrl: String,
    val channelName: String,
    val channelAvatar: String,
    val views: Int,
    val likes: Int,
    val duration: String,
    val category: String, // "ترفيه", "وثائقي", "تعليمي", "ألعاب"
    val isShort: Boolean = false,
    val isSubscribed: Boolean = false,
    val isLiked: Boolean = false,
    val isDownloaded: Boolean = false,
    val liveStatus: String = "NONE", // "NONE", "LIVE"
    val liveViewerCount: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val videoId: String,
    val author: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)
