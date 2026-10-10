package com.example.data.model

data class Song(
    val id: String,
    val mediaStoreId: Long = 0L,
    val title: String,
    val artist: String,
    val album: String,
    val albumArtist: String = "",
    val composer: String = "",
    val genre: String = "Unknown",
    val year: Int = 0,
    val durationMs: Long = 0L,
    val trackNumber: Int = 0,
    val uri: String,
    val albumArtUri: String? = null,
    val mimeType: String = "audio/mpeg",
    val bitrate: Int = 320,
    val sampleRate: Int = 44100,
    val channels: Int = 2,
    val isFavorite: Boolean = false,
    val playCount: Int = 0,
    val lastPlayedTimestamp: Long = 0L,
    val dateAdded: Long = 0L
) {
    val formattedDuration: String
        get() {
            val totalSeconds = durationMs / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return "%d:%02d".format(minutes, seconds)
        }
}
