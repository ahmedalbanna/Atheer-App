package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.CommentEntity
import com.example.data.VideoEntity
import com.example.data.VideoRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AppScreen {
    HOME,
    SHORTS,
    CREATE,
    SUBSCRIPTIONS,
    LIBRARY,
    VIDEO_DETAIL,
    SEARCH
}

class VideoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: VideoRepository
    
    val allVideos: StateFlow<List<VideoEntity>>
    val shorts: StateFlow<List<VideoEntity>>
    val subscribedVideos: StateFlow<List<VideoEntity>>
    val downloadedVideos: StateFlow<List<VideoEntity>>

    // Search and Catalog states
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("الكل")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // Navigation and Active Playing Video
    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _activeVideo = MutableStateFlow<VideoEntity?>(null)
    val activeVideo: StateFlow<VideoEntity?> = _activeVideo.asStateFlow()

    // Comments for active video
    val activeComments: StateFlow<List<CommentEntity>> = _activeVideo
        .filterNotNull()
        .flatMapLatest { video ->
            repository.observeComments(video.id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Backstack simulation for easy navigation
    private val screenHistory = mutableListOf<AppScreen>()

    init {
        val database = AppDatabase.getDatabase(application)
        val videoDao = database.videoDao()
        repository = VideoRepository(videoDao)

        allVideos = repository.allVideos
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        shorts = repository.shorts
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        subscribedVideos = repository.subscribedVideos
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        downloadedVideos = repository.downloadedVideos
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        // Seed the database in a background coroutine
        viewModelScope.launch {
            repository.seedDatabaseIfEmpty()
        }
    }

    // Navigation triggers
    fun navigateTo(screen: AppScreen, activeVideoItem: VideoEntity? = null) {
        if (screen == AppScreen.VIDEO_DETAIL && activeVideoItem != null) {
            _activeVideo.value = activeVideoItem
            viewModelScope.launch {
                repository.incrementViews(activeVideoItem.id)
            }
        }
        if (_currentScreen.value != screen) {
            screenHistory.add(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun handleBackPress(): Boolean {
        if (screenHistory.isNotEmpty()) {
            val prev = screenHistory.removeAt(screenHistory.size - 1)
            _currentScreen.value = prev
            return true
        }
        return false
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Toggle actions
    fun toggleLike(videoId: String, currentlyLiked: Boolean) {
        viewModelScope.launch {
            repository.toggleLike(videoId, !currentlyLiked)
            // Update active video if it is currently playing to reflect status
            _activeVideo.value?.let { current ->
                if (current.id == videoId) {
                    _activeVideo.value = current.copy(
                        isLiked = !currentlyLiked,
                        likes = current.likes + (if (!currentlyLiked) 1 else -1)
                    )
                }
            }
        }
    }

    fun toggleSubscription(videoId: String, currentlySubscribed: Boolean) {
        viewModelScope.launch {
            repository.toggleSubscription(videoId, !currentlySubscribed)
            _activeVideo.value?.let { current ->
                if (current.id == videoId) {
                    _activeVideo.value = current.copy(isSubscribed = !currentlySubscribed)
                }
            }
        }
    }

    fun toggleDownload(videoId: String, currentlyDownloaded: Boolean) {
        viewModelScope.launch {
            repository.toggleDownload(videoId, !currentlyDownloaded)
            _activeVideo.value?.let { current ->
                if (current.id == videoId) {
                    _activeVideo.value = current.copy(isDownloaded = !currentlyDownloaded)
                }
            }
        }
    }

    fun addComment(videoId: String, author: String, text: String) {
        viewModelScope.launch {
            repository.addComment(videoId, author, text)
        }
    }

    // Create / Upload Video functionality
    fun uploadVideo(
        title: String,
        description: String,
        category: String,
        isShort: Boolean,
        videoUrl: String,
        thumbnailUrl: String
    ) {
        viewModelScope.launch {
            val randomId = "uploaded_${System.currentTimeMillis()}"
            val finalVideoUrl = if (videoUrl.isNotBlank()) videoUrl else "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
            val finalThumbnailUrl = if (thumbnailUrl.isNotBlank()) thumbnailUrl else "https://images.unsplash.com/photo-1611162617213-7d7a39e9b1d7?w=800&auto=format&fit=crop"
            
            val newVideo = VideoEntity(
                id = randomId,
                title = title,
                description = description,
                videoUrl = finalVideoUrl,
                thumbnailUrl = finalThumbnailUrl,
                channelName = "قناتي الخاصة",
                channelAvatar = "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=100&auto=format&fit=crop",
                views = 1,
                likes = 1,
                duration = if (isShort) "0:15" else "4:32",
                category = category,
                isShort = isShort,
                timestamp = System.currentTimeMillis()
            )
            repository.insertVideo(newVideo)
        }
    }
}
