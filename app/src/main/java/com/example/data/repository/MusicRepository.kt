package com.example.data.repository

import com.example.core.database.dao.PlaylistDao
import com.example.core.database.dao.SongDao
import com.example.core.database.entity.PlaylistEntity
import com.example.core.database.entity.PlaylistSongCrossRef
import com.example.core.database.entity.SongEntity
import com.example.data.model.Album
import com.example.data.model.Artist
import com.example.data.model.Playlist
import com.example.data.model.Song
import com.example.data.source.MediaStoreScanner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class MusicRepository(
    private val songDao: SongDao,
    private val playlistDao: PlaylistDao,
    private val scanner: MediaStoreScanner
) {

    val allSongs: Flow<List<Song>> = songDao.getAllSongs().map { entities ->
        entities.map { it.toSong() }
    }

    val favoriteSongs: Flow<List<Song>> = songDao.getFavoriteSongs().map { entities ->
        entities.map { it.toSong() }
    }

    val recentlyAdded: Flow<List<Song>> = songDao.getRecentlyAdded(20).map { entities ->
        entities.map { it.toSong() }
    }

    val recentlyPlayed: Flow<List<Song>> = songDao.getRecentlyPlayed(20).map { entities ->
        entities.map { it.toSong() }
    }

    val mostPlayed: Flow<List<Song>> = songDao.getMostPlayed(20).map { entities ->
        entities.map { it.toSong() }
    }

    val albums: Flow<List<Album>> = allSongs.map { songs ->
        songs.groupBy { it.album }.map { (albumTitle, albumSongs) ->
            val first = albumSongs.first()
            Album(
                id = albumTitle,
                title = albumTitle,
                artist = first.artist,
                year = first.year,
                songCount = albumSongs.size,
                totalDurationMs = albumSongs.sumOf { it.durationMs },
                artworkUri = first.albumArtUri
            )
        }.sortedBy { it.title }
    }

    val artists: Flow<List<Artist>> = allSongs.map { songs ->
        songs.groupBy { it.artist }.map { (artistName, artistSongs) ->
            val albumsCount = artistSongs.map { it.album }.distinct().size
            Artist(
                id = artistName,
                name = artistName,
                albumCount = albumsCount,
                songCount = artistSongs.size,
                artworkUri = artistSongs.firstOrNull()?.albumArtUri
            )
        }.sortedBy { it.name }
    }

    val playlists: Flow<List<Playlist>> = playlistDao.getAllPlaylists().map { entities ->
        entities.map { entity ->
            val count = playlistDao.getSongCount(entity.id)
            entity.toPlaylist(count)
        }
    }

    fun searchSongs(query: String): Flow<List<Song>> = songDao.searchSongs(query).map { entities ->
        entities.map { it.toSong() }
    }

    suspend fun refreshLibrary() = withContext(Dispatchers.IO) {
        val scanned = scanner.scanLocalAudio()
        if (scanned.isNotEmpty()) {
            val entities = scanned.map { SongEntity.fromSong(it) }
            songDao.insertSongs(entities)
        }
    }

    suspend fun toggleFavorite(songId: String, currentFavorite: Boolean) = withContext(Dispatchers.IO) {
        songDao.updateFavorite(songId, !currentFavorite)
    }

    suspend fun recordSongPlayed(songId: String) = withContext(Dispatchers.IO) {
        songDao.incrementPlayCount(songId, System.currentTimeMillis())
    }

    suspend fun createPlaylist(name: String): Long = withContext(Dispatchers.IO) {
        playlistDao.insertPlaylist(PlaylistEntity(name = name))
    }

    suspend fun addSongToPlaylist(playlistId: Long, songId: String) = withContext(Dispatchers.IO) {
        val count = playlistDao.getSongCount(playlistId)
        playlistDao.addSongToPlaylist(
            PlaylistSongCrossRef(playlistId = playlistId, songId = songId, orderIndex = count)
        )
    }

    fun getSongsForPlaylist(playlistId: Long): Flow<List<Song>> {
        return playlistDao.getSongsForPlaylist(playlistId).map { entities ->
            entities.map { it.toSong() }
        }
    }
}
