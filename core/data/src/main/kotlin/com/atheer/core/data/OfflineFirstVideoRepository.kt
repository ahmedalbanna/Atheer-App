package com.atheer.core.data

import com.atheer.core.database.CommentEntity
import com.atheer.core.database.CommentDao
import com.atheer.core.database.VideoDao
import com.atheer.core.database.VideoEntity
import com.atheer.core.database.asExternalModel
import com.atheer.core.model.Comment
import com.atheer.core.model.Video
import com.atheer.core.model.VideoCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OfflineFirstVideoRepository @Inject constructor(
    private val videoDao: VideoDao,
    private val commentDao: CommentDao,
    private val timeSource: TimeSource,
) : VideoRepository {

    override fun observeAllVideos(): Flow<List<Video>> =
        videoDao.observeAll().map { entities -> entities.map { it.asExternalModel() } }

    override fun observeShorts(): Flow<List<Video>> =
        videoDao.observeShorts().map { entities -> entities.map { it.asExternalModel() } }

    override fun observeSubscribedVideos(): Flow<List<Video>> =
        videoDao.observeSubscribed().map { entities -> entities.map { it.asExternalModel() } }

    override fun observeDownloadedVideos(): Flow<List<Video>> =
        videoDao.observeDownloaded().map { entities -> entities.map { it.asExternalModel() } }

    override fun observeVideo(id: String): Flow<Video?> =
        videoDao.observeById(id).map { it?.asExternalModel() }

    override fun observeComments(videoId: String): Flow<List<Comment>> =
        commentDao.observeForVideo(videoId).map { entities -> entities.map { it.asExternalModel() } }

    override suspend fun getVideo(id: String): Video? = videoDao.getById(id)?.asExternalModel()

    /**
     * Single source of truth: Room emits the updated row and every collector
     * (list + detail + shorts) re-renders. UI never applies optimistic deltas.
     */
    override suspend fun setLiked(videoId: String, isLiked: Boolean) {
        videoDao.setLiked(videoId, isLiked)
    }

    /** Subscribing to one video of a channel subscribes to the whole channel. */
    override suspend fun setSubscribed(videoId: String, isSubscribed: Boolean) {
        val video = videoDao.getById(videoId) ?: return
        videoDao.setChannelSubscription(video.channelName, isSubscribed)
    }

    override suspend fun setDownloaded(videoId: String, isDownloaded: Boolean) {
        videoDao.setDownloaded(videoId, isDownloaded)
    }

    override suspend fun incrementViews(videoId: String) {
        videoDao.incrementViews(videoId)
    }

    override suspend fun addComment(videoId: String, author: String, text: String) {
        commentDao.upsert(
            CommentEntity(
                videoId = videoId,
                author = author,
                text = text,
                timestamp = timeSource.now(),
            ),
        )
    }

    override suspend fun uploadVideo(
        title: String,
        description: String,
        category: VideoCategory,
        isShort: Boolean,
        videoUrl: String?,
        thumbnailUrl: String?,
    ) {
        videoDao.upsert(
            VideoEntity(
                id = "uploaded_${timeSource.now()}",
                title = title,
                description = description,
                videoUrl = videoUrl?.takeIf { it.isNotBlank() }
                    ?: "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                thumbnailUrl = thumbnailUrl?.takeIf { it.isNotBlank() }
                    ?: "https://images.unsplash.com/photo-1611162617213-7d7a39e9b1d7?w=800&auto=format&fit=crop",
                channelName = MY_CHANNEL_NAME,
                channelAvatar = MY_CHANNEL_AVATAR,
                views = 1,
                likes = 1,
                duration = if (isShort) "0:15" else "4:32",
                category = category,
                isShort = isShort,
                timestamp = timeSource.now(),
            ),
        )
    }

    /** Idempotent demo-content bootstrap; invoked once from app startup. */
    suspend fun seedIfEmpty() {
        if (videoDao.count() > 0) return
        val now = timeSource.now()
        videoDao.upsertAll(
            DemoContent.videos.mapIndexed { index, video ->
                video.copy(timestamp = now - index * 60_000L).toEntity()
            },
        )
        commentDao.upsertAll(
            DemoContent.comments.map { (videoId, authorToText) ->
                CommentEntity(
                    videoId = videoId,
                    author = authorToText.first,
                    text = authorToText.second,
                    timestamp = now,
                )
            },
        )
    }

    companion object {
        const val MY_CHANNEL_NAME = "قناتي الخاصة"
        const val MY_CHANNEL_AVATAR =
            "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=100&auto=format&fit=crop"

        private fun Video.toEntity() = VideoEntity(
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
    }
}
