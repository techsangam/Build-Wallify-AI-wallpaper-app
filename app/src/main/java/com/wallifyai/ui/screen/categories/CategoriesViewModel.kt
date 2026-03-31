package com.wallifyai.ui.screen.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wallifyai.data.repository.WallpaperRepository
import com.wallifyai.domain.model.UserActionType
import com.wallifyai.domain.model.Wallpaper
import com.wallifyai.domain.model.WallpaperCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val wallpaperRepository: WallpaperRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoriesUiState())
    val uiState: StateFlow<CategoriesUiState> = _uiState.asStateFlow()

    private var currentPage = 1

    init {
        viewModelScope.launch {
            wallpaperRepository.observeFavoriteIds().collect { favorites ->
                _uiState.update { current -> current.copy(favoriteIds = favorites) }
            }
        }
        refresh()
    }

    fun refresh() {
        currentPage = 1
        _uiState.update {
            it.copy(
                isLoading = true,
                isLoadingMore = false,
                endReached = false,
                errorMessage = null,
            )
        }

        viewModelScope.launch {
            val result = runCatching {
                wallpaperRepository.getWallpapers(
                    category = _uiState.value.selectedCategory,
                    page = 1,
                )
            }
            val wallpapers = result.getOrDefault(emptyList())
            _uiState.update {
                it.copy(
                    wallpapers = wallpapers,
                    isLoading = false,
                    endReached = wallpapers.size < WallpaperRepository.DEFAULT_PAGE_SIZE,
                    errorMessage = if (wallpapers.isEmpty()) {
                        result.exceptionOrNull()?.message ?: "No cached wallpapers available yet."
                    } else {
                        null
                    },
                )
            }
        }
    }

    fun selectCategory(category: WallpaperCategory) {
        if (_uiState.value.selectedCategory == category) return
        _uiState.update { it.copy(selectedCategory = category) }
        refresh()
    }

    fun loadMore() {
        val state = _uiState.value
        if (state.isLoading || state.isLoadingMore || state.endReached) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }
            val nextPage = currentPage + 1
            val result = runCatching {
                wallpaperRepository.getWallpapers(
                    category = _uiState.value.selectedCategory,
                    page = nextPage,
                )
            }
            val wallpapers = result.getOrDefault(emptyList())
            if (wallpapers.isNotEmpty()) currentPage = nextPage

            _uiState.update {
                it.copy(
                    wallpapers = it.wallpapers + wallpapers,
                    isLoadingMore = false,
                    endReached = wallpapers.size < WallpaperRepository.DEFAULT_PAGE_SIZE,
                    errorMessage = if (it.wallpapers.isEmpty()) result.exceptionOrNull()?.message else null,
                )
            }
        }
    }

    fun onWallpaperOpened(wallpaper: Wallpaper) {
        viewModelScope.launch {
            wallpaperRepository.recordActivity(wallpaper, UserActionType.CLICK)
        }
    }

    fun toggleFavorite(wallpaper: Wallpaper) {
        viewModelScope.launch {
            wallpaperRepository.toggleFavorite(wallpaper)
        }
    }
}

data class CategoriesUiState(
    val selectedCategory: WallpaperCategory = WallpaperCategory.ALL,
    val wallpapers: List<Wallpaper> = emptyList(),
    val favoriteIds: Set<String> = emptySet(),
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val endReached: Boolean = false,
    val errorMessage: String? = null,
)
