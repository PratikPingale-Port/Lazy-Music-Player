package com.example.ui.screens.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Album
import com.example.data.model.Artist
import com.example.data.model.Song
import com.example.data.repository.MusicRepository
import com.example.playback.controller.ResonancePlayerController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SortOrder {
    TITLE, ARTIST, DURATION, DATE_ADDED
}

class LibraryViewModel(
    private val musicRepository: MusicRepository,
    val playerController: ResonancePlayerController
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _sortOrder = MutableStateFlow(SortOrder.TITLE)
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()

    private val _selectedTab = MutableStateFlow(0) // 0: Songs, 1: Albums, 2: Artists
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    val filteredSongs: StateFlow<List<Song>> = combine(
        musicRepository.allSongs,
        _searchQuery,
        _sortOrder
    ) { songs, query, sort ->
        val filtered = if (query.isBlank()) {
            songs
        } else {
            songs.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.artist.contains(query, ignoreCase = true) ||
                it.album.contains(query, ignoreCase = true)
            }
        }

        when (sort) {
            SortOrder.TITLE -> filtered.sortedBy { it.title.lowercase() }
            SortOrder.ARTIST -> filtered.sortedBy { it.artist.lowercase() }
            SortOrder.DURATION -> filtered.sortedByDescending { it.durationMs }
            SortOrder.DATE_ADDED -> filtered.sortedByDescending { it.dateAdded }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val albums: StateFlow<List<Album>> = musicRepository.albums
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val artists: StateFlow<List<Artist>> = musicRepository.artists
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
    }

    fun setSelectedTab(index: Int) {
        _selectedTab.value = index
    }

    fun playSong(song: Song, queue: List<Song>) {
        viewModelScope.launch {
            playerController.playSong(song, queue)
            musicRepository.recordSongPlayed(song.id)
        }
    }

    fun toggleFavorite(song: Song) {
        viewModelScope.launch {
            musicRepository.toggleFavorite(song.id, song.isFavorite)
        }
    }
}
