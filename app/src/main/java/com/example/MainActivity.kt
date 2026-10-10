package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.core.audio.AudioEffectManager
import com.example.core.database.ResonanceDatabase
import com.example.data.repository.EqualizerRepository
import com.example.data.repository.MusicRepository
import com.example.data.source.MediaStoreScanner
import com.example.playback.controller.ResonancePlayerController
import com.example.ui.components.MiniPlayer
import com.example.ui.navigation.Screen
import com.example.ui.screens.equalizer.EqualizerScreen
import com.example.ui.screens.equalizer.EqualizerViewModel
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.home.HomeViewModel
import com.example.ui.screens.library.LibraryScreen
import com.example.ui.screens.library.LibraryViewModel
import com.example.ui.screens.player.FullPlayerScreen
import com.example.ui.screens.playlists.PlaylistsScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.ResonanceTheme
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevatedDark
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var audioEffectManager: AudioEffectManager
    private lateinit var playerController: ResonancePlayerController
    private lateinit var musicRepository: MusicRepository
    private lateinit var equalizerRepository: EqualizerRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = ResonanceDatabase.getInstance(applicationContext)
        val scanner = MediaStoreScanner(applicationContext)
        musicRepository = MusicRepository(db.songDao(), db.playlistDao(), scanner)
        equalizerRepository = EqualizerRepository(db.eqPresetDao())

        audioEffectManager = AudioEffectManager()
        playerController = ResonancePlayerController(applicationContext, audioEffectManager)

        setContent {
            ResonanceTheme {
                MainContent(
                    musicRepository = musicRepository,
                    equalizerRepository = equalizerRepository,
                    audioEffectManager = audioEffectManager,
                    playerController = playerController
                )
            }
        }
    }

    override fun onDestroy() {
        playerController.release()
        super.onDestroy()
    }
}

@Composable
fun MainContent(
    musicRepository: MusicRepository,
    equalizerRepository: EqualizerRepository,
    audioEffectManager: AudioEffectManager,
    playerController: ResonancePlayerController
) {
    val context = LocalContext.current
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
    var isFullPlayerVisible by remember { mutableStateOf(false) }

    val playbackState by playerController.playbackState.collectAsStateWithLifecycle()

    // Runtime Permission Request for Media Audio & Notifications
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.values.any { it }
        if (granted) {
            CoroutineScope(Dispatchers.IO).launch {
                musicRepository.refreshLibrary()
            }
        }
    }

    LaunchedEffect(Unit) {
        val permissionsToRequest = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.READ_MEDIA_AUDIO)
            }
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .statusBarsPadding()
    ) {
        Scaffold(
            containerColor = SurfaceDark,
            bottomBar = {
                Column(modifier = Modifier.navigationBarsPadding()) {
                    // Persistent Mini Player above Navigation Bar
                    AnimatedVisibility(
                        visible = playbackState.currentSong != null && !isFullPlayerVisible,
                        enter = slideInVertically { it },
                        exit = slideOutVertically { it }
                    ) {
                        MiniPlayer(
                            playbackState = playbackState,
                            onMiniPlayerClick = { isFullPlayerVisible = true },
                            onPlayPauseClick = { playerController.togglePlayPause() },
                            onNextClick = { playerController.playNext() }
                        )
                    }

                    // Bottom Navigation Bar
                    NavigationBar(
                        containerColor = SurfaceElevatedDark,
                        tonalElevation = 8.dp
                    ) {
                        Screen.bottomNavScreens.forEach { screen ->
                            val isSelected = currentScreen.route == screen.route
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { currentScreen = screen },
                                icon = {
                                    Icon(
                                        imageVector = screen.icon,
                                        contentDescription = screen.title
                                    )
                                },
                                label = {
                                    Text(
                                        text = screen.title,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = ObsidianBlack,
                                    selectedTextColor = CyanNeon,
                                    indicatorColor = CyanNeon,
                                    unselectedIconColor = TextSecondary,
                                    unselectedTextColor = TextSecondary
                                ),
                                modifier = Modifier.testTag("nav_item_${screen.route}")
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (currentScreen) {
                    Screen.Home -> {
                        val homeViewModel = remember { HomeViewModel(musicRepository, playerController) }
                        HomeScreen(
                            viewModel = homeViewModel,
                            playbackState = playbackState,
                            onNavigateToPlayer = { isFullPlayerVisible = true }
                        )
                    }
                    Screen.Library -> {
                        val libraryViewModel = remember { LibraryViewModel(musicRepository, playerController) }
                        LibraryScreen(
                            viewModel = libraryViewModel,
                            playbackState = playbackState
                        )
                    }
                    Screen.Playlists -> {
                        PlaylistsScreen(musicRepository = musicRepository)
                    }
                    Screen.Equalizer -> {
                        val eqViewModel = remember { EqualizerViewModel(audioEffectManager, equalizerRepository) }
                        EqualizerScreen(viewModel = eqViewModel)
                    }
                    Screen.Settings -> {
                        SettingsScreen()
                    }
                    else -> Unit
                }
            }
        }

        // Full-Screen Player Overlay
        AnimatedVisibility(
            visible = isFullPlayerVisible && playbackState.currentSong != null,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            FullPlayerScreen(
                playerController = playerController,
                playbackState = playbackState,
                onNavigateBack = { isFullPlayerVisible = false },
                onNavigateToEqualizer = {
                    isFullPlayerVisible = false
                    currentScreen = Screen.Equalizer
                },
                onToggleFavorite = { songId, currentFav ->
                    CoroutineScope(Dispatchers.IO).launch {
                        musicRepository.toggleFavorite(songId, currentFav)
                    }
                }
            )
        }
    }
}
