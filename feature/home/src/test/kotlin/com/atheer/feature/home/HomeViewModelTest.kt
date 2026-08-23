package com.atheer.feature.home

import app.cash.turbine.test
import com.atheer.core.data.VideoRepository
import com.atheer.core.model.Comment
import com.atheer.core.model.Video
import com.atheer.core.model.VideoCategory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeVideoRepository
    private lateinit var viewModel: HomeViewModel

    private val longVideo = Video(
        id = "v1",
        title = "long",
        description = "",
        videoUrl = "",
        thumbnailUrl = "",
        channelName = "ch1",
        channelAvatar = "",
        category = VideoCategory.ENTERTAINMENT,
    )
    private val shortVideo = longVideo.copy(id = "s1", isShort = true)
    private val docVideo = longVideo.copy(id = "v2", category = VideoCategory.DOCUMENTARY)

    @Before
    fun setUp() {
        fakeRepository = FakeVideoRepository(listOf(longVideo, shortVideo, docVideo))
        viewModel = HomeViewModel(fakeRepository)
    }

    @Test
    fun `initial state filters shorts and defaults to ALL`() = runTest(testDispatcher) {
        viewModel.uiState.test {
            var state = awaitItem()
            while (state.isLoading) {
                advanceUntilIdle()
                state = awaitItem()
            }
            assertTrue(state.videos.none { it.isShort })
            assertEquals(2, state.videos.size)
            assertEquals(VideoCategory.ALL, state.selectedCategory)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `category selection filters videos`() = runTest(testDispatcher) {
        advanceUntilIdle()

        viewModel.onCategorySelect(VideoCategory.DOCUMENTARY)

        viewModel.uiState.test {
            var state = awaitItem()
            while (state.isLoading || state.selectedCategory != VideoCategory.DOCUMENTARY) {
                advanceUntilIdle()
                state = awaitItem()
            }
            assertEquals(listOf("v2"), state.videos.map { it.id })
            cancelAndIgnoreRemainingEvents()
        }
    }
}

class FakeVideoRepository(initial: List<Video> = emptyList()) : VideoRepository {

    private val videos = MutableStateFlow(initial)

    override fun observeAllVideos(): Flow<List<Video>> = videos

    override fun observeShorts(): Flow<List<Video>> =
        MutableStateFlow(videos.value.filter { it.isShort })

    override fun observeSubscribedVideos(): Flow<List<Video>> =
        MutableStateFlow(videos.value.filter { it.isSubscribed })

    override fun observeDownloadedVideos(): Flow<List<Video>> =
        MutableStateFlow(videos.value.filter { it.isDownloaded })

    override fun observeVideo(id: String): Flow<Video?> =
        MutableStateFlow(videos.value.firstOrNull { it.id == id })

    override fun observeComments(videoId: String): Flow<List<Comment>> =
        MutableStateFlow(emptyList())

    override suspend fun getVideo(id: String): Video? = videos.value.firstOrNull { it.id == id }

    override suspend fun setLiked(videoId: String, isLiked: Boolean) =
        videos.update { list -> list.map { if (it.id == videoId) it.copy(isLiked = isLiked) else it } }

    override suspend fun setSubscribed(videoId: String, isSubscribed: Boolean) =
        videos.update { list ->
            list.map { if (it.id == videoId) it.copy(isSubscribed = isSubscribed) else it }
        }

    override suspend fun setDownloaded(videoId: String, isDownloaded: Boolean) =
        videos.update { list ->
            list.map { if (it.id == videoId) it.copy(isDownloaded = isDownloaded) else it }
        }

    override suspend fun incrementViews(videoId: String) = Unit

    override suspend fun addComment(videoId: String, author: String, text: String) = Unit

    override suspend fun uploadVideo(
        title: String,
        description: String,
        category: VideoCategory,
        isShort: Boolean,
        videoUrl: String?,
        thumbnailUrl: String?,
    ) = Unit
}
