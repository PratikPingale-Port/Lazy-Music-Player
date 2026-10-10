package com.example.data.model

data class Album(
    val id: String,
    val title: String,
    val artist: String,
    val year: Int = 0,
    val songCount: Int = 0,
    val totalDurationMs: Long = 0L,
    val artworkUri: String? = null
)
