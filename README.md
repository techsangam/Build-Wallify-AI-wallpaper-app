# Wallify AI

Wallify AI is a Kotlin Android app built with Jetpack Compose, MVVM, Hilt, Room, Retrofit, Coil, and WorkManager.

## Highlights
- Wallhaven-powered wallpaper feed with pagination and category browsing
- Full-screen preview with download, favorite, and wallpaper apply actions
- Room-backed user activity tracking and recommendation scoring
- Favorites persistence and cached metadata for offline-friendly browsing
- Daily auto-wallpaper scheduling with category and time selection
- Light, dark, and system theme support

## Project Structure
- `app/src/main/java/com/wallifyai/data`: Retrofit, Room, repositories, and system services
- `app/src/main/java/com/wallifyai/domain`: app models and navigation metadata
- `app/src/main/java/com/wallifyai/ui`: Compose screens, theme, navigation, and reusable components
- `app/src/main/java/com/wallifyai/worker`: WorkManager scheduling and background wallpaper updates

## Setup
1. Open the project in Android Studio.
2. Make sure your Android SDK is installed.
3. Add your Wallhaven API key to `local.properties`:
   `WALLHAVEN_API_KEY=your_key_here`
4. Sync Gradle and run the app.

## Build
- Debug APK output:
  `app/build/outputs/apk/debug/app-debug.apk`

## Notes
- `local.properties` is intentionally ignored by Git because it contains machine-specific values.
- The project is configured to build with Gradle 8.7 and AGP 8.5.2.
