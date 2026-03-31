package com.wallifyai.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavDestination(
    val route: String,
    val label: String,
    val icon: ImageVector,
) {
    companion object {
        val items = listOf(
            BottomNavDestination(route = "home", label = "Home", icon = Icons.Rounded.Home),
            BottomNavDestination(route = "categories", label = "Categories", icon = Icons.Rounded.Category),
            BottomNavDestination(route = "favorites", label = "Favorites", icon = Icons.Rounded.Favorite),
            BottomNavDestination(route = "settings", label = "Settings", icon = Icons.Rounded.Settings),
        )
    }
}
