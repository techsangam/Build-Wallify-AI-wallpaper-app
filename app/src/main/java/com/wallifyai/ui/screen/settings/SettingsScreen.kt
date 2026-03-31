package com.wallifyai.ui.screen.settings

import android.app.TimePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wallifyai.domain.model.ThemeMode
import com.wallifyai.domain.model.WallpaperCategory
import com.wallifyai.ui.component.GradientHero
import com.wallifyai.ui.component.SectionTitle
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsRoute(
    contentPadding: PaddingValues,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = contentPadding.calculateTopPadding() + innerPadding.calculateTopPadding() + 16.dp,
                bottom = contentPadding.calculateBottomPadding() + 88.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                GradientHero(
                    title = "Settings",
                    subtitle = "Tune the daily wallpaper engine, theme, and cache behavior to match your routine.",
                )
            }

            item {
                SectionTitle(
                    title = "Auto wallpaper",
                    subtitle = "Run a background refresh every 24 hours with your preferred category.",
                )
            }

            item {
                Surface(shape = RoundedCornerShape(24.dp), tonalElevation = 3.dp) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(text = "Enable daily wallpaper", style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = "When enabled, WorkManager fetches a fresh wallpaper and applies it automatically.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Switch(
                            checked = uiState.settings.autoWallpaperEnabled,
                            onCheckedChange = viewModel::setAutoWallpaperEnabled,
                        )
                        Text(
                            text = "Preferred category",
                            style = MaterialTheme.typography.titleSmall,
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            WallpaperCategory.feedCategories.forEach { category ->
                                val selected = category == uiState.settings.autoWallpaperCategory
                                AssistChip(
                                    onClick = { viewModel.setAutoWallpaperCategory(category) },
                                    label = { Text(text = category.title) },
                                    colors = if (selected) {
                                        AssistChipDefaults.assistChipColors(
                                            containerColor = MaterialTheme.colorScheme.primary,
                                            labelColor = MaterialTheme.colorScheme.onPrimary,
                                        )
                                    } else {
                                        AssistChipDefaults.assistChipColors()
                                    },
                                )
                            }
                        }
                        OutlinedButton(
                            onClick = {
                                TimePickerDialog(
                                    context,
                                    { _, hour, minute -> viewModel.setAutoWallpaperTime(hour, minute) },
                                    uiState.settings.autoWallpaperHour,
                                    uiState.settings.autoWallpaperMinute,
                                    false,
                                ).show()
                            },
                        ) {
                            Text(
                                text = String.format(
                                    Locale.getDefault(),
                                    "Change time: %02d:%02d",
                                    uiState.settings.autoWallpaperHour,
                                    uiState.settings.autoWallpaperMinute,
                                ),
                            )
                        }
                    }
                }
            }

            item {
                SectionTitle(
                    title = "Appearance",
                    subtitle = "Switch between system, light, and dark modes instantly.",
                )
            }

            item {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    ThemeMode.entries.forEach { themeMode ->
                        val selected = themeMode == uiState.settings.themeMode
                        AssistChip(
                            onClick = { viewModel.setThemeMode(themeMode) },
                            label = { Text(text = themeMode.name.lowercase().replaceFirstChar { it.titlecase() }) },
                            colors = if (selected) {
                                AssistChipDefaults.assistChipColors(
                                    containerColor = MaterialTheme.colorScheme.secondary,
                                    labelColor = MaterialTheme.colorScheme.onSecondary,
                                )
                            } else {
                                AssistChipDefaults.assistChipColors()
                            },
                        )
                    }
                }
            }

            item {
                SectionTitle(
                    title = "Maintenance",
                    subtitle = "Clear cached wallpaper metadata and Coil image caches when you want a clean slate.",
                )
            }

            item {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = viewModel::clearCache,
                    enabled = !uiState.isClearingCache,
                ) {
                    if (uiState.isClearingCache) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(18.dp),
                            strokeWidth = 2.dp,
                        )
                    }
                    Text(text = "Clear cache")
                }
            }
        }
    }
}
