package com.wallifyai.ui.screen.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wallifyai.domain.model.Wallpaper
import com.wallifyai.domain.model.WallpaperCategory
import com.wallifyai.ui.component.CategoryChipRow
import com.wallifyai.ui.component.EmptyStateCard
import com.wallifyai.ui.component.GradientHero
import com.wallifyai.ui.component.SectionTitle
import com.wallifyai.ui.component.ShimmerWallpaperCard
import com.wallifyai.ui.component.WallpaperCard
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun CategoriesRoute(
    contentPadding: PaddingValues,
    onOpenPreview: (Wallpaper) -> Unit,
    viewModel: CategoriesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val gridState = rememberLazyGridState()

    LaunchedEffect(gridState) {
        snapshotFlow {
            val layoutInfo = gridState.layoutInfo
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleItem >= layoutInfo.totalItemsCount - 6
        }
            .distinctUntilChanged()
            .collect { shouldLoadMore ->
                if (shouldLoadMore) viewModel.loadMore()
            }
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(160.dp),
        state = gridState,
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = contentPadding.calculateTopPadding() + 16.dp,
            bottom = contentPadding.calculateBottomPadding() + 88.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            GradientHero(
                title = "Explore Categories",
                subtitle = "Switch between trending shots and focused collections for each mood.",
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            CategoryChipRow(
                categories = listOf(WallpaperCategory.ALL) + WallpaperCategory.feedCategories,
                selectedCategory = uiState.selectedCategory,
                onCategorySelected = viewModel::selectCategory,
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            SectionTitle(
                title = uiState.selectedCategory.title,
                subtitle = "Paged results with Coil caching and offline-friendly metadata fallback.",
            )
        }

        if (uiState.isLoading && uiState.wallpapers.isEmpty()) {
            items(6) {
                ShimmerWallpaperCard(modifier = Modifier.height(240.dp))
            }
        } else if (uiState.wallpapers.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                EmptyStateCard(
                    title = "No wallpapers available",
                    subtitle = uiState.errorMessage ?: "Try another category or come back after the first sync.",
                )
            }
        } else {
            items(uiState.wallpapers, key = { it.id }) { wallpaper ->
                WallpaperCard(
                    wallpaper = wallpaper,
                    modifier = Modifier.height(240.dp),
                    isFavorite = wallpaper.id in uiState.favoriteIds,
                    onClick = {
                        viewModel.onWallpaperOpened(wallpaper)
                        onOpenPreview(wallpaper)
                    },
                    onFavoriteClick = { viewModel.toggleFavorite(wallpaper) },
                )
            }
        }

        if (uiState.isLoadingMore) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
