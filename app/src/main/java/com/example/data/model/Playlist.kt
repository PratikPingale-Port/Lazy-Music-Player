package com.example.data.model

data class Playlist(
    val id: Long = 0,
    val name: String,
    val songCount: Int = 0,
    val createdTimestamp: Long = System.currentTimeMillis()
)
