package com.wallifyai.ui.screen.preview

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wallifyai.data.repository.WallpaperRepository
import com.wallifyai.data.service.WallpaperDownloadManager
import com.wallifyai.data.service.WallpaperManagerHelper
import com.wallifyai.domain.model.UserActionType
import com.wallifyai.domain.model.Wallpaper
import com.wallifyai.domain.model.WallpaperDestination
import com.wallifyai.domain.model.WallpaperScaleMode
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class PreviewViewModel @Inject constructor(
    private val wallpaperRepository: WallpaperRepository,
    private val wallpaperDownloadManager: WallpaperDownloadManager,
    private val wallpaperManagerHelper: WallpaperManagerHelper,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PreviewUiState())
    val uiState: StateFlow<PreviewUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<String>()
    val events: SharedFlow<String> = _events.asSharedFlow()

    private var favoriteObserverJob: Job? = null
    private var boundWallpaperId: String? = null

    fun bind(wallpaper: Wallpaper) {
        if (boundWallpaperId == wallpaper.id) return
        boundWallpaperId = wallpaper.id
        favoriteObserverJob?.cancel()
        favoriteObserverJob = viewModelScope.launch {
            wallpaperRepository.observeIsFavorite(wallpaper.id).collect { isFavorite ->
                _uiState.update { it.copy(isFavorite = isFavorite) }
            }
        }
    }

    fun toggleFavorite(wallpaper: Wallpaper) {
        runAction {
            val isFavorite = wallpaperRepository.toggleFavorite(wallpaper)
            emitMessage(if (isFavorite) "Added to favorites" else "Removed from favorites")
        }
    }

    fun downloadWallpaper(wallpaper: Wallpaper) {
        runAction {
            val uri: Uri = wallpaperDownloadManager.downloadWallpaper(wallpaper)
            wallpaperRepository.recordActivity(wallpaper, UserActionType.DOWNLOAD)
            emitMessage("Wallpaper saved to ${uri.path ?: "gallery"}")
        }
    }

    fun setWallpaper(
        wallpaper: Wallpaper,
        destination: WallpaperDestination,
        scaleMode: WallpaperScaleMode,
    ) {
        runAction {
            wallpaperManagerHelper.setWallpaper(
                wallpaper = wallpaper,
                destination = destination,
                scaleMode = scaleMode,
            )
            emitMessage("Wallpaper applied successfully")
        }
    }

    private fun runAction(block: suspend () -> Unit) {
        if (_uiState.value.isBusy) return
        viewModelScope.launch {
            _uiState.update { it.copy(isBusy = true) }
            runCatching { block() }
                .onFailure { throwable ->
                    emitMessage(throwable.message ?: "Something went wrong.")
                }
            _uiState.update { it.copy(isBusy = false) }
        }
    }

    private suspend fun emitMessage(message: String) {
        _events.emit(message)
    }
}

data class PreviewUiState(
    val isFavorite: Boolean = false,
    val isBusy: Boolean = false,
)
