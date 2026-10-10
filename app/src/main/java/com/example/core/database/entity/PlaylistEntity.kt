package com.example.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.Playlist

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val createdTimestamp: Long = System.currentTimeMillis()
) {
    fun toPlaylist(songCount: Int = 0): Playlist = Playlist(
        id = id,
        name = name,
        songCount = songCount,
        createdTimestamp = createdTimestamp
    )
}
