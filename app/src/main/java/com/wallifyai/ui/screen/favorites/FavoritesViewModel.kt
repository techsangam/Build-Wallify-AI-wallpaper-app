package com.wallifyai.ui.screen.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wallifyai.data.repository.WallpaperRepository
import com.wallifyai.domain.model.Wallpaper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    wallpaperRepository: WallpaperRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            wallpaperRepository.observeFavorites().collect { wallpapers ->
                _uiState.update {
                    it.copy(
                        wallpapers = wallpapers,
                        isLoading = false,
                    )
                }
            }
        }
    }
}

data class FavoritesUiState(
    val wallpapers: List<Wallpaper> = emptyList(),
    val isLoading: Boolean = true,
)
