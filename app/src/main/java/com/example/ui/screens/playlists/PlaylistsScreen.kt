package com.example.ui.screens.playlists

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Playlist
import com.example.data.repository.MusicRepository
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevatedDark
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun PlaylistsScreen(
    musicRepository: MusicRepository,
    modifier: Modifier = Modifier
) {
    val playlists by musicRepository.playlists.collectAsStateWithLifecycle(initialValue = emptyList())
    val favorites by musicRepository.favoriteSongs.collectAsStateWithLifecycle(initialValue = emptyList())
    val mostPlayed by musicRepository.mostPlayed.collectAsStateWithLifecycle(initialValue = emptyList())
    val recentlyAdded by musicRepository.recentlyAdded.collectAsStateWithLifecycle(initialValue = emptyList())

    val scope = rememberCoroutineScope()
    var showCreateDialog by remember { mutableStateOf(false) }
    var playlistTitle by remember { mutableStateOf("") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = SurfaceDark,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = CyanNeon,
                contentColor = ObsidianBlack,
                modifier = Modifier
                    .padding(bottom = 60.dp)
                    .testTag("create_playlist_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Playlist")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Playlists & Smart Collections",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Automatic & Custom Curations",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }

            // Smart collections
            item {
                Text(
                    text = "Smart Collections",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            item {
                SmartCollectionRow(
                    title = "Favorites",
                    subtitle = "${favorites.size} tracks",
                    icon = Icons.Default.Favorite,
                    iconTint = AmberGlow
                )
            }

            item {
                SmartCollectionRow(
                    title = "Most Played",
                    subtitle = "${mostPlayed.size} tracks",
                    icon = Icons.Default.Whatshot,
                    iconTint = CyanNeon
                )
            }

            item {
                SmartCollectionRow(
                    title = "Recently Added",
                    subtitle = "${recentlyAdded.size} tracks",
                    icon = Icons.Default.History,
                    iconTint = CyanNeon
                )
            }

            // User Playlists
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Custom Playlists",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (playlists.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = SurfaceElevatedDark,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("No custom playlists yet", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
                            Text("Tap the + button to create a new playlist", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            } else {
                items(playlists) { playlist ->
                    PlaylistRow(playlist = playlist)
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("New Playlist") },
            text = {
                OutlinedTextField(
                    value = playlistTitle,
                    onValueChange = { playlistTitle = it },
                    label = { Text("Playlist Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (playlistTitle.isNotBlank()) {
                            scope.launch {
                                musicRepository.createPlaylist(playlistTitle.trim())
                                playlistTitle = ""
                                showCreateDialog = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanNeon)
                ) {
                    Text("Create", color = ObsidianBlack, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SmartCollectionRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: androidx.compose.ui.graphics.Color
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = SurfaceElevatedDark,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
        }
    }
}

@Composable
fun PlaylistRow(playlist: Playlist) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = SurfaceElevatedDark,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(CyanNeon.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.QueueMusic, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(24.dp))
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(playlist.name, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                Text("${playlist.songCount} tracks", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
        }
    }
}
