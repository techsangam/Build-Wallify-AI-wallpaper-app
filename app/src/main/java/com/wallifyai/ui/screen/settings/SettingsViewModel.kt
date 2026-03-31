package com.wallifyai.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wallifyai.data.repository.SettingsRepository
import com.wallifyai.data.repository.WallpaperRepository
import com.wallifyai.data.service.ImageCacheManager
import com.wallifyai.domain.model.ThemeMode
import com.wallifyai.domain.model.UserSettings
import com.wallifyai.domain.model.WallpaperCategory
import com.wallifyai.worker.AutoWallpaperScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val wallpaperRepository: WallpaperRepository,
    private val imageCacheManager: ImageCacheManager,
    private val autoWallpaperScheduler: AutoWallpaperScheduler,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<String>()
    val events: SharedFlow<String> = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            settingsRepository.settingsFlow.collect { settings ->
                _uiState.update { it.copy(settings = settings) }
            }
        }
    }

    fun setAutoWallpaperEnabled(enabled: Boolean) {
        val updatedSettings = _uiState.value.settings.copy(autoWallpaperEnabled = enabled)
        viewModelScope.launch {
            settingsRepository.setAutoWallpaperEnabled(enabled)
            syncScheduler(updatedSettings)
            _events.emit(if (enabled) "Daily wallpaper enabled" else "Daily wallpaper disabled")
        }
    }

    fun setAutoWallpaperCategory(category: WallpaperCategory) {
        val updatedSettings = _uiState.value.settings.copy(autoWallpaperCategory = category)
        viewModelScope.launch {
            settingsRepository.setAutoWallpaperCategory(category)
            syncScheduler(updatedSettings)
            _events.emit("Auto wallpaper category set to ${category.title}")
        }
    }

    fun setAutoWallpaperTime(hour: Int, minute: Int) {
        val updatedSettings = _uiState.value.settings.copy(
            autoWallpaperHour = hour,
            autoWallpaperMinute = minute,
        )
        viewModelScope.launch {
            settingsRepository.setAutoWallpaperTime(hour, minute)
            syncScheduler(updatedSettings)
            _events.emit("Daily wallpaper time updated")
        }
    }

    fun setThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            settingsRepository.setThemeMode(themeMode)
        }
    }

    fun clearCache() {
        if (_uiState.value.isClearingCache) return
        viewModelScope.launch {
            _uiState.update { it.copy(isClearingCache = true) }
            runCatching {
                wallpaperRepository.clearCachedMetadata()
                imageCacheManager.clearCaches()
            }.onSuccess {
                _events.emit("Cache cleared")
            }.onFailure { throwable ->
                _events.emit(throwable.message ?: "Unable to clear cache")
            }
            _uiState.update { it.copy(isClearingCache = false) }
        }
    }

    private fun syncScheduler(settings: UserSettings) {
        if (settings.autoWallpaperEnabled) {
            autoWallpaperScheduler.schedule(
                hour = settings.autoWallpaperHour,
                minute = settings.autoWallpaperMinute,
            )
        } else {
            autoWallpaperScheduler.cancel()
        }
    }
}

data class SettingsUiState(
    val settings: UserSettings = UserSettings(),
    val isClearingCache: Boolean = false,
)
