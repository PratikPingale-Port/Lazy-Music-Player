package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Library : Screen("library", "Library", Icons.Default.LibraryMusic)
    object Playlists : Screen("playlists", "Playlists", Icons.Default.QueueMusic)
    object Equalizer : Screen("equalizer", "Equalizer", Icons.Default.Tune)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    object FullPlayer : Screen("full_player", "Now Playing", Icons.Default.Tune)

    companion object {
        val bottomNavScreens = listOf(Home, Library, Playlists, Equalizer, Settings)
    }
}
