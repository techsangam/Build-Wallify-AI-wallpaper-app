package com.wallifyai.domain.model

data class UserSettings(
    val autoWallpaperEnabled: Boolean = false,
    val autoWallpaperCategory: WallpaperCategory = WallpaperCategory.NATURE,
    val autoWallpaperHour: Int = 8,
    val autoWallpaperMinute: Int = 0,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
)

