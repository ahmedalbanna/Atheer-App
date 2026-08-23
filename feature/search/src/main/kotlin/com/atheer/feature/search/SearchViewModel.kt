package com.atheer.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atheer.core.data.VideoRepository
import com.atheer.core.model.Video
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    videoRepository: VideoRepository,
) : ViewModel() {

    private val query = MutableStateFlow("")

    val searchQuery: StateFlow<String> = query.asStateFlow()

    val results: StateFlow<List<Video>> = combine(
        videoRepository.observeAllVideos(),
        query.debounce(200),
    ) { videos, q ->
        val pool = videos.filterNot { it.isShort }
        if (q.isBlank()) {
            pool
        } else {
            pool.filter { video ->
                video.title.contains(q, ignoreCase = true) ||
                    video.description.contains(q, ignoreCase = true) ||
                    video.channelName.contains(q, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun onQueryChange(value: String) {
        query.value = value
    }
}
