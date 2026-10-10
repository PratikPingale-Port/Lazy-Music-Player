package com.example.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.data.model.Song

@Entity(
    tableName = "songs",
    indices = [
        Index(value = ["title"]),
        Index(value = ["artist"]),
        Index(value = ["album"]),
        Index(value = ["isFavorite"]),
        Index(value = ["playCount"]),
        Index(value = ["dateAdded"])
    ]
)
data class SongEntity(
    @PrimaryKey val id: String,
    val mediaStoreId: Long,
    val title: String,
    val artist: String,
    val album: String,
    val albumArtist: String,
    val composer: String,
    val genre: String,
    val year: Int,
    val durationMs: Long,
    val trackNumber: Int,
    val uri: String,
    val albumArtUri: String?,
    val mimeType: String,
    val bitrate: Int,
    val sampleRate: Int,
    val channels: Int,
    val isFavorite: Boolean,
    val playCount: Int,
    val lastPlayedTimestamp: Long,
    val dateAdded: Long
) {
    fun toSong(): Song = Song(
        id = id,
        mediaStoreId = mediaStoreId,
        title = title,
        artist = artist,
        album = album,
        albumArtist = albumArtist,
        composer = composer,
        genre = genre,
        year = year,
        durationMs = durationMs,
        trackNumber = trackNumber,
        uri = uri,
        albumArtUri = albumArtUri,
        mimeType = mimeType,
        bitrate = bitrate,
        sampleRate = sampleRate,
        channels = channels,
        isFavorite = isFavorite,
        playCount = playCount,
        lastPlayedTimestamp = lastPlayedTimestamp,
        dateAdded = dateAdded
    )

    companion object {
        fun fromSong(song: Song): SongEntity = SongEntity(
            id = song.id,
            mediaStoreId = song.mediaStoreId,
            title = song.title,
            artist = song.artist,
            album = song.album,
            albumArtist = song.albumArtist,
            composer = song.composer,
            genre = song.genre,
            year = song.year,
            durationMs = song.durationMs,
            trackNumber = song.trackNumber,
            uri = song.uri,
            albumArtUri = song.albumArtUri,
            mimeType = song.mimeType,
            bitrate = song.bitrate,
            sampleRate = song.sampleRate,
            channels = song.channels,
            isFavorite = song.isFavorite,
            playCount = song.playCount,
            lastPlayedTimestamp = song.lastPlayedTimestamp,
            dateAdded = song.dateAdded
        )
    }
}
