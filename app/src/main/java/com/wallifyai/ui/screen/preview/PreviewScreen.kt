package com.wallifyai.ui.screen.preview

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Wallpaper
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.wallifyai.domain.model.Wallpaper
import com.wallifyai.domain.model.WallpaperDestination
import com.wallifyai.domain.model.WallpaperScaleMode
import com.wallifyai.ui.component.EmptyStateCard

@Composable
fun PreviewRoute(
    wallpaper: Wallpaper?,
    onBack: () -> Unit,
    viewModel: PreviewViewModel = hiltViewModel(),
) {
    if (wallpaper == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            EmptyStateCard(
                title = "Preview unavailable",
                subtitle = "Open a wallpaper from the feed again to restore the preview context.",
            )
        }
        return
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    var showWallpaperDialog by rememberSaveable { mutableStateOf(false) }
    var selectedDestination by rememberSaveable { mutableStateOf(WallpaperDestination.BOTH) }
    var selectedScaleMode by rememberSaveable { mutableStateOf(WallpaperScaleMode.CROP) }

    LaunchedEffect(wallpaper.id) {
        viewModel.bind(wallpaper)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            viewModel.downloadWallpaper(wallpaper)
        } else {
            Toast.makeText(context, "Storage permission is required on older Android versions.", Toast.LENGTH_SHORT).show()
        }
    }

    fun startDownload() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            viewModel.downloadWallpaper(wallpaper)
            return
        }

        val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
        if (ContextCompat.checkSelfPermission(context, permission) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            viewModel.downloadWallpaper(wallpaper)
        } else {
            permissionLauncher.launch(permission)
        }
    }

    if (showWallpaperDialog) {
        AlertDialog(
            onDismissRequest = { showWallpaperDialog = false },
            title = { Text(text = "Apply wallpaper") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(text = "Choose where the wallpaper should be applied.")
                    WallpaperDestination.entries.forEach { destination ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedDestination == destination,
                                onClick = { selectedDestination = destination },
                            )
                            Text(text = destination.name.lowercase().replaceFirstChar { it.titlecase() })
                        }
                    }
                    Text(text = "Choose how the image should fit the screen.")
                    WallpaperScaleMode.entries.forEach { scaleMode ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedScaleMode == scaleMode,
                                onClick = { selectedScaleMode = scaleMode },
                            )
                            Text(text = scaleMode.name.lowercase().replaceFirstChar { it.titlecase() })
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showWallpaperDialog = false
                        viewModel.setWallpaper(
                            wallpaper = wallpaper,
                            destination = selectedDestination,
                            scaleMode = selectedScaleMode,
                        )
                    },
                ) {
                    Text(text = "Apply")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showWallpaperDialog = false }) {
                    Text(text = "Cancel")
                }
            },
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = ImageRequest.Builder(context)
                    .data(wallpaper.fullUrl)
                    .crossfade(true)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .build(),
                contentDescription = wallpaper.description,
                contentScale = ContentScale.Crop,
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0x66000000),
                                Color.Transparent,
                                Color(0xCC000000),
                            ),
                        ),
                    ),
            )

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                        )
                    }
                    if (uiState.isBusy) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding(),
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
                    tonalElevation = 8.dp,
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(18.dp),
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = wallpaper.description,
                                style = MaterialTheme.typography.headlineSmall,
                            )
                            Text(
                                text = "by ${wallpaper.authorName}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            FilledTonalButton(
                                modifier = Modifier.weight(1f),
                                onClick = { startDownload() },
                                enabled = !uiState.isBusy,
                            ) {
                                Icon(Icons.Rounded.Download, contentDescription = null)
                                Text(text = " Download")
                            }
                            FilledTonalButton(
                                modifier = Modifier.weight(1f),
                                onClick = { showWallpaperDialog = true },
                                enabled = !uiState.isBusy,
                            ) {
                                Icon(Icons.Rounded.Wallpaper, contentDescription = null)
                                Text(text = " Set")
                            }
                        }

                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { viewModel.toggleFavorite(wallpaper) },
                            enabled = !uiState.isBusy,
                        ) {
                            Icon(
                                imageVector = if (uiState.isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                                contentDescription = null,
                            )
                            Text(text = if (uiState.isFavorite) " Remove from Favorites" else " Add to Favorites")
                        }
                    }
                }
            }
        }
    }
}
