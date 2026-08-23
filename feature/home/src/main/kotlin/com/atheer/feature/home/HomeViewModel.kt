package com.atheer.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atheer.core.data.VideoRepository
import com.atheer.core.model.Video
import com.atheer.core.model.VideoCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class HomeUiState(
    val videos: List<Video> = emptyList(),
    val categories: List<VideoCategory> = VideoCategory.entries.toList(),
    val selectedCategory: VideoCategory = VideoCategory.ALL,
    val isLoading: Boolean = true,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    videoRepository: VideoRepository,
) : ViewModel() {

    private val selectedCategory =
        kotlinx.coroutines.flow.MutableStateFlow(VideoCategory.ALL)

    val uiState: StateFlow<HomeUiState> = combine(
        videoRepository.observeAllVideos(),
        selectedCategory,
    ) { videos, category ->
        HomeUiState(
            videos = videos.filter {
                !it.isShort && (category == VideoCategory.ALL || it.category == category)
            },
            categories = VideoCategory.entries.toList(),
            selectedCategory = category,
            isLoading = false,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState(),
    )

    fun onCategorySelect(category: VideoCategory) {
        selectedCategory.value = category
    }
}
