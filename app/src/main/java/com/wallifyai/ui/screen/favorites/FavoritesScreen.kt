package com.wallifyai.ui.screen.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wallifyai.domain.model.Wallpaper
import com.wallifyai.ui.component.EmptyStateCard
import com.wallifyai.ui.component.GradientHero
import com.wallifyai.ui.component.ShimmerWallpaperCard
import com.wallifyai.ui.component.WallpaperCard

@Composable
fun FavoritesRoute(
    contentPadding: PaddingValues,
    onOpenPreview: (Wallpaper) -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LazyVerticalGrid(
        columns = GridCells.Adaptive(160.dp),
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
                title = "Favorites",
                subtitle = "Your saved wallpaper collection stays available even when you go offline.",
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                text = "Saved wallpapers",
                style = MaterialTheme.typography.titleLarge,
            )
        }

        if (uiState.isLoading) {
            items(6) {
                ShimmerWallpaperCard(modifier = Modifier.height(240.dp))
            }
        } else if (uiState.wallpapers.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                EmptyStateCard(
                    title = "Nothing favorited yet",
                    subtitle = "Tap the heart on any wallpaper to pin it here for quick access.",
                )
            }
        } else {
            items(uiState.wallpapers, key = { it.id }) { wallpaper ->
                WallpaperCard(
                    wallpaper = wallpaper,
                    modifier = Modifier.height(240.dp),
                    isFavorite = true,
                    onClick = { onOpenPreview(wallpaper) },
                )
            }
        }
    }
}
