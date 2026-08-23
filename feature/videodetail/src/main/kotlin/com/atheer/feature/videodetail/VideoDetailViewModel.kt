package com.atheer.feature.videodetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atheer.core.data.VideoRepository
import com.atheer.core.model.Comment
import com.atheer.core.model.Video
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class VideoDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val videoRepository: VideoRepository,
) : ViewModel() {

    val videoId: String = savedStateHandle.get<String>("videoId").orEmpty()

    val video: StateFlow<Video?> = videoRepository.observeVideo(videoId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val comments: StateFlow<List<Comment>> = videoRepository.observeComments(videoId)
        .map { it.reversed() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val relatedVideos: StateFlow<List<Video>> = videoRepository.observeAllVideos()
        .map { videos -> videos.filter { it.id != videoId && !it.isShort } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        viewModelScope.launch {
            if (videoId.isNotBlank()) videoRepository.incrementViews(videoId)
        }
    }

    fun onLike(video: Video) {
        viewModelScope.launch { videoRepository.setLiked(video.id, !video.isLiked) }
    }

    fun onSubscribe(video: Video) {
        viewModelScope.launch { videoRepository.setSubscribed(video.id, !video.isSubscribed) }
    }

    fun onDownloadToggle(video: Video, onFinished: () -> Unit) {
        viewModelScope.launch {
            // Simulated transfer; a real implementation would use WorkManager.
            if (!video.isDownloaded) {
                delay(1_500)
                videoRepository.setDownloaded(video.id, true)
            } else {
                videoRepository.setDownloaded(video.id, false)
            }
            onFinished()
        }
    }

    fun onCommentSubmit(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            videoRepository.addComment(videoId, "أنا (المستخدم)", text.trim())
        }
    }
}
