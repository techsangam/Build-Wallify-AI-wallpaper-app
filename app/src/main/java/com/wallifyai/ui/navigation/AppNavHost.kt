package com.wallifyai.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.wallifyai.domain.model.AppDestination
import com.wallifyai.domain.model.Wallpaper
import com.wallifyai.ui.screen.categories.CategoriesRoute
import com.wallifyai.ui.screen.favorites.FavoritesRoute
import com.wallifyai.ui.screen.home.HomeRoute
import com.wallifyai.ui.screen.preview.PreviewRoute
import com.wallifyai.ui.screen.settings.SettingsRoute

@Composable
fun AppNavHost(
    navController: NavHostController,
    innerPadding: PaddingValues,
) {
    NavHost(
        navController = navController,
        startDestination = AppDestination.HOME,
    ) {
        composable(AppDestination.HOME) {
            HomeRoute(
                contentPadding = innerPadding,
                onOpenPreview = { wallpaper ->
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        AppDestination.PREVIEW_WALLPAPER_KEY,
                        wallpaper,
                    )
                    navController.navigate(AppDestination.PREVIEW)
                },
            )
        }
        composable(AppDestination.CATEGORIES) {
            CategoriesRoute(
                contentPadding = innerPadding,
                onOpenPreview = { wallpaper ->
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        AppDestination.PREVIEW_WALLPAPER_KEY,
                        wallpaper,
                    )
                    navController.navigate(AppDestination.PREVIEW)
                },
            )
        }
        composable(AppDestination.FAVORITES) {
            FavoritesRoute(
                contentPadding = innerPadding,
                onOpenPreview = { wallpaper ->
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        AppDestination.PREVIEW_WALLPAPER_KEY,
                        wallpaper,
                    )
                    navController.navigate(AppDestination.PREVIEW)
                },
            )
        }
        composable(AppDestination.SETTINGS) {
            SettingsRoute(contentPadding = innerPadding)
        }
        composable(AppDestination.PREVIEW) {
            val wallpaper = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<Wallpaper>(AppDestination.PREVIEW_WALLPAPER_KEY)

            PreviewRoute(
                wallpaper = wallpaper,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
