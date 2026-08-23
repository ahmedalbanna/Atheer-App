package com.atheer.feature.subscriptions

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
class SubscriptionsViewModel @Inject constructor(
    videoRepository: VideoRepository,
) : ViewModel() {

    val subscribedVideos: StateFlow<List<Video>> = videoRepository.observeSubscribedVideos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
