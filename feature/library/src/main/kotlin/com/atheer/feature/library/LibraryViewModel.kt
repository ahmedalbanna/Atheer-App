package com.atheer.feature.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atheer.core.data.VideoRepository
import com.atheer.core.model.Video
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    videoRepository: VideoRepository,
) : ViewModel() {

    val downloadedVideos: StateFlow<List<Video>> = videoRepository.observeDownloadedVideos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val recentVideos: StateFlow<List<Video>> = videoRepository.observeAllVideos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
