package com.wallifyai.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wallifyai.BuildConfig
import com.wallifyai.data.repository.WallpaperRepository
import com.wallifyai.domain.model.UserActionType
import com.wallifyai.domain.model.Wallpaper
import com.wallifyai.domain.model.WallpaperCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val wallpaperRepository: WallpaperRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

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
        val category = _uiState.value.selectedCategory
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
            val recommendedDeferred = async { wallpaperRepository.getRecommendedWallpapers() }
            val wallpaperResult = runCatching {
                wallpaperRepository.getWallpapers(category = category, page = 1)
            }
            val recommended = runCatching { recommendedDeferred.await() }.getOrDefault(emptyList())
            val wallpapers = wallpaperResult.getOrDefault(emptyList())
            val errorMessage = when {
                wallpapers.isNotEmpty() -> null
                BuildConfig.WALLHAVEN_API_KEY.isBlank() ->
                    "Add WALLHAVEN_API_KEY to local.properties to unlock authenticated Wallhaven access."
                else -> wallpaperResult.exceptionOrNull()?.message ?: "Unable to load wallpapers right now."
            }

            _uiState.update {
                it.copy(
                    recommended = recommended,
                    wallpapers = wallpapers,
                    isLoading = false,
                    endReached = wallpapers.size < WallpaperRepository.DEFAULT_PAGE_SIZE,
                    errorMessage = errorMessage,
                )
            }
        }
    }

    fun selectCategory(category: WallpaperCategory) {
        if (category == _uiState.value.selectedCategory) return
        _uiState.update { it.copy(selectedCategory = category) }
        refresh()
    }

    fun loadMore() {
        val state = _uiState.value
        if (state.isLoading || state.isLoadingMore || state.endReached) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }
            val nextPage = currentPage + 1
            val moreResult = runCatching {
                wallpaperRepository.getWallpapers(
                    category = _uiState.value.selectedCategory,
                    page = nextPage,
                )
            }
            val moreWallpapers = moreResult.getOrDefault(emptyList())
            if (moreWallpapers.isNotEmpty()) {
                currentPage = nextPage
            }

            _uiState.update {
                it.copy(
                    wallpapers = it.wallpapers + moreWallpapers,
                    isLoadingMore = false,
                    endReached = moreWallpapers.size < WallpaperRepository.DEFAULT_PAGE_SIZE,
                    errorMessage = if (it.wallpapers.isEmpty()) {
                        moreResult.exceptionOrNull()?.message
                    } else {
                        it.errorMessage
                    },
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

data class HomeUiState(
    val selectedCategory: WallpaperCategory = WallpaperCategory.NATURE,
    val recommended: List<Wallpaper> = emptyList(),
    val wallpapers: List<Wallpaper> = emptyList(),
    val favoriteIds: Set<String> = emptySet(),
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val endReached: Boolean = false,
    val errorMessage: String? = null,
)
