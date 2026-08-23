package com.atheer.core.database

import com.atheer.core.model.Comment
import com.atheer.core.model.Video

fun VideoEntity.asExternalModel(): Video = Video(
    id = id,
    title = title,
    description = description,
    videoUrl = videoUrl,
    thumbnailUrl = thumbnailUrl,
    channelName = channelName,
    channelAvatar = channelAvatar,
    views = views,
    likes = likes,
    duration = duration,
    category = category,
    isShort = isShort,
    isSubscribed = isSubscribed,
    isLiked = isLiked,
    isDownloaded = isDownloaded,
    isLive = isLive,
    liveViewerCount = liveViewerCount,
    timestamp = timestamp,
)

fun CommentEntity.asExternalModel(): Comment = Comment(
    id = id,
    videoId = videoId,
    author = author,
    text = text,
    timestamp = timestamp,
)
