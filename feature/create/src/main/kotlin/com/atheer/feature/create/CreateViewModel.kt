package com.atheer.feature.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atheer.core.data.VideoRepository
import com.atheer.core.model.VideoCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CreateUiState(
    val title: String = "",
    val description: String = "",
    val category: VideoCategory = VideoCategory.ENTERTAINMENT,
    val isShort: Boolean = false,
    val videoUrl: String = "",
    val thumbnailUrl: String = "",
    val isPublished: Boolean = false,
) {
    val canPublish: Boolean get() = title.isNotBlank()
}

@HiltViewModel
class CreateViewModel @Inject constructor(
    private val videoRepository: VideoRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateUiState())
    val uiState: StateFlow<CreateUiState> = _uiState.asStateFlow()

    fun onTitleChange(value: String) {
        _uiState.value = _uiState.value.copy(title = value)
    }

    fun onDescriptionChange(value: String) {
        _uiState.value = _uiState.value.copy(description = value)
    }

    fun onCategoryChange(category: VideoCategory) {
        _uiState.value = _uiState.value.copy(category = category)
    }

    fun onTypeChange(isShort: Boolean) {
        _uiState.value = _uiState.value.copy(isShort = isShort)
    }

    fun onVideoUrlChange(value: String) {
        _uiState.value = _uiState.value.copy(videoUrl = value)
    }

    fun onThumbnailUrlChange(value: String) {
        _uiState.value = _uiState.value.copy(thumbnailUrl = value)
    }

    fun publish(onSuccess: () -> Unit = {}) {
        val state = _uiState.value
        if (!state.canPublish || state.isPublished) return

        viewModelScope.launch {
            videoRepository.uploadVideo(
                title = state.title.trim(),
                description = state.description.trim(),
                category = state.category,
                isShort = state.isShort,
                videoUrl = state.videoUrl,
                thumbnailUrl = state.thumbnailUrl,
            )
            _uiState.value = state.copy(isPublished = true)
            onSuccess()
        }
    }

    fun resetForm() {
        _uiState.value = CreateUiState()
    }
}
