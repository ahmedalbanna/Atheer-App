package com.atheer.feature.shorts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atheer.core.data.VideoRepository
import com.atheer.core.model.Comment
import com.atheer.core.model.Video
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class ShortsViewModel @Inject constructor(
    private val videoRepository: VideoRepository,
) : ViewModel() {

    val shorts: StateFlow<List<Video>> = videoRepository.observeShorts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val activeShortId =
        kotlinx.coroutines.flow.MutableStateFlow<String?>(null)

    val comments: StateFlow<List<Comment>> = activeShortId
        .flatMapLatest { id -> if (id == null) flowOf(emptyList()) else videoRepository.observeComments(id) }
        .map { comments -> comments.reversed() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun onPageChanged(video: Video?) {
        activeShortId.value = video?.id
    }

    fun onLike(video: Video) {
        viewModelScope.launch { videoRepository.setLiked(video.id, !video.isLiked) }
    }

    fun onSubscribe(video: Video) {
        viewModelScope.launch { videoRepository.setSubscribed(video.id, !video.isSubscribed) }
    }

    fun onCommentSubmit(video: Video, text: String) {
        viewModelScope.launch {
            videoRepository.addComment(video.id, "أنا (المستخدم)", text)
        }
    }
}
