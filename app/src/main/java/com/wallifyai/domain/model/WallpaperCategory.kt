package com.wallifyai.domain.model

enum class WallpaperCategory(
    val title: String,
    val query: String,
    val storageKey: String,
) {
    ALL(title = "Trending", query = "", storageKey = "all"),
    NATURE(title = "Nature", query = "nature landscape", storageKey = "nature"),
    CARS(title = "Cars", query = "cars automotive", storageKey = "cars"),
    TECH(title = "Tech", query = "technology futuristic", storageKey = "tech"),
    ABSTRACT(title = "Abstract", query = "abstract minimal", storageKey = "abstract"),
    ANIMALS(title = "Animals", query = "animals wildlife", storageKey = "animals"),
    ;

    val apiQuery: String?
        get() = query.takeIf { it.isNotBlank() }

    companion object {
        val feedCategories: List<WallpaperCategory>
            get() = listOf(NATURE, CARS, TECH, ABSTRACT, ANIMALS)

        fun fromStorageKey(value: String?): WallpaperCategory {
            return entries.firstOrNull { it.storageKey.equals(value, ignoreCase = true) } ?: NATURE
        }
    }
}
