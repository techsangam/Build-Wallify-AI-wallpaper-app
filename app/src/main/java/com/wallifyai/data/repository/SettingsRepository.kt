package com.wallifyai.data.repository

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.wallifyai.domain.model.ThemeMode
import com.wallifyai.domain.model.UserSettings
import com.wallifyai.domain.model.WallpaperCategory
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.settingsDataStore by preferencesDataStore(name = "wallify_preferences")

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    val settingsFlow: Flow<UserSettings> = context.settingsDataStore.data.map { preferences ->
        UserSettings(
            autoWallpaperEnabled = preferences[Keys.AUTO_WALLPAPER_ENABLED] ?: false,
            autoWallpaperCategory = WallpaperCategory.fromStorageKey(
                preferences[Keys.AUTO_WALLPAPER_CATEGORY],
            ),
            autoWallpaperHour = preferences[Keys.AUTO_WALLPAPER_HOUR] ?: 8,
            autoWallpaperMinute = preferences[Keys.AUTO_WALLPAPER_MINUTE] ?: 0,
            themeMode = preferences[Keys.THEME_MODE]
                ?.let { stored -> ThemeMode.entries.firstOrNull { it.name == stored } }
                ?: ThemeMode.SYSTEM,
        )
    }

    suspend fun setAutoWallpaperEnabled(enabled: Boolean) {
        updatePreference(Keys.AUTO_WALLPAPER_ENABLED, enabled)
    }

    suspend fun setAutoWallpaperCategory(category: WallpaperCategory) {
        updatePreference(Keys.AUTO_WALLPAPER_CATEGORY, category.storageKey)
    }

    suspend fun setAutoWallpaperTime(hour: Int, minute: Int) {
        context.settingsDataStore.edit { preferences ->
            preferences[Keys.AUTO_WALLPAPER_HOUR] = hour
            preferences[Keys.AUTO_WALLPAPER_MINUTE] = minute
        }
    }

    suspend fun setThemeMode(themeMode: ThemeMode) {
        updatePreference(Keys.THEME_MODE, themeMode.name)
    }

    private suspend fun <T> updatePreference(key: Preferences.Key<T>, value: T) {
        context.settingsDataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    private object Keys {
        val AUTO_WALLPAPER_ENABLED = booleanPreferencesKey("auto_wallpaper_enabled")
        val AUTO_WALLPAPER_CATEGORY = stringPreferencesKey("auto_wallpaper_category")
        val AUTO_WALLPAPER_HOUR = intPreferencesKey("auto_wallpaper_hour")
        val AUTO_WALLPAPER_MINUTE = intPreferencesKey("auto_wallpaper_minute")
        val THEME_MODE = stringPreferencesKey("theme_mode")
    }
}

