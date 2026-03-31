package com.wallifyai.domain.model

enum class WallpaperCategory(
    val title: String,
    val query: String,
    val storageKey: String,
) {
    ALL(title = "Trending", query = "", storageKey = "all"),
    NATURE(title = "Nature", query = "nature wallpaper", storageKey = "nature"),
    CARS(title = "Cars", query = "super cars wallpaper", storageKey = "cars"),
    TECH(title = "Tech", query = "technology wallpaper", storageKey = "tech"),
    ABSTRACT(title = "Abstract", query = "abstract art wallpaper", storageKey = "abstract"),
    ANIMALS(title = "Animals", query = "animals wallpaper", storageKey = "animals"),
    ;

    companion object {
        val feedCategories: List<WallpaperCategory>
            get() = listOf(NATURE, CARS, TECH, ABSTRACT, ANIMALS)

        fun fromStorageKey(value: String?): WallpaperCategory {
            return entries.firstOrNull { it.storageKey.equals(value, ignoreCase = true) } ?: NATURE
        }
    }
}

