package com.example.playback.controller

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.example.core.audio.AudioEffectManager
import com.example.data.model.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ResonancePlayerController(
    private val context: Context,
    val audioEffectManager: AudioEffectManager
) {

    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    private var player: ExoPlayer? = null
    private val handler = Handler(Looper.getMainLooper())
    private val scope = CoroutineScope(Dispatchers.Main)

    private val progressUpdater = object : Runnable {
        override fun run() {
            player?.let { p ->
                if (p.isPlaying) {
                    val pos = p.currentPosition
                    val dur = if (p.duration > 0) p.duration else _playbackState.value.durationMs
                    _playbackState.value = _playbackState.value.copy(
                        currentPositionMs = pos,
                        durationMs = dur
                    )
                }
            }
            handler.postDelayed(this, 300)
        }
    }

    @OptIn(UnstableApi::class)
    fun initPlayer() {
        if (player != null) return

        val audioAttributes = AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()

        val p = ExoPlayer.Builder(context)
            .setAudioAttributes(audioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .build()

        p.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _playbackState.value = _playbackState.value.copy(isPlaying = isPlaying)
            }

            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_READY) {
                    val dur = p.duration
                    if (dur > 0) {
                        _playbackState.value = _playbackState.value.copy(durationMs = dur)
                    }
                    audioEffectManager.bindAudioSession(p.audioSessionId)
                    _playbackState.value = _playbackState.value.copy(audioSessionId = p.audioSessionId)
                } else if (state == Player.STATE_ENDED) {
                    handleTrackEnded()
                }
            }
        })

        player = p
        handler.post(progressUpdater)
    }

    fun playSong(song: Song, newQueue: List<Song> = emptyList()) {
        initPlayer()
        val p = player ?: return

        val queue = if (newQueue.isNotEmpty()) newQueue else listOf(song)
        val index = queue.indexOfFirst { it.id == song.id }.let { if (it >= 0) it else 0 }

        _playbackState.value = _playbackState.value.copy(
            currentSong = song,
            queue = queue,
            currentIndex = index,
            durationMs = song.durationMs,
            currentPositionMs = 0L
        )

        val mediaItem = MediaItem.Builder()
            .setUri(Uri.parse(song.uri))
            .setMediaId(song.id)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(song.title)
                    .setArtist(song.artist)
                    .setAlbumTitle(song.album)
                    .setArtworkUri(song.albumArtUri?.let { Uri.parse(it) })
                    .build()
            )
            .build()

        p.setMediaItem(mediaItem)
        p.prepare()
        p.play()
    }

    fun togglePlayPause() {
        val p = player ?: return
        if (p.isPlaying) {
            p.pause()
        } else {
            p.play()
        }
    }

    fun playNext() {
        val state = _playbackState.value
        if (state.queue.isEmpty()) return

        val nextIndex = when {
            state.isShuffle -> (state.queue.indices).random()
            state.currentIndex + 1 < state.queue.size -> state.currentIndex + 1
            state.repeatMode == RepeatMode.ALL -> 0
            else -> return
        }

        val nextSong = state.queue[nextIndex]
        playSong(nextSong, state.queue)
    }

    fun playPrevious() {
        val state = _playbackState.value
        val p = player ?: return

        // If played more than 3 seconds, restart current track
        if (p.currentPosition > 3000) {
            seekTo(0)
            return
        }

        if (state.queue.isEmpty()) return

        val prevIndex = when {
            state.currentIndex - 1 >= 0 -> state.currentIndex - 1
            state.repeatMode == RepeatMode.ALL -> state.queue.size - 1
            else -> 0
        }

        val prevSong = state.queue[prevIndex]
        playSong(prevSong, state.queue)
    }

    fun seekTo(positionMs: Long) {
        player?.seekTo(positionMs)
        _playbackState.value = _playbackState.value.copy(currentPositionMs = positionMs)
    }

    fun toggleShuffle() {
        _playbackState.value = _playbackState.value.copy(
            isShuffle = !_playbackState.value.isShuffle
        )
    }

    fun toggleRepeat() {
        val nextRepeat = when (_playbackState.value.repeatMode) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
        _playbackState.value = _playbackState.value.copy(repeatMode = nextRepeat)
    }

    fun setPlaybackSpeed(speed: Float) {
        val clamped = speed.coerceIn(0.5f, 2.0f)
        player?.setPlaybackSpeed(clamped)
        _playbackState.value = _playbackState.value.copy(playbackSpeed = clamped)
    }

    fun addToQueue(song: Song) {
        val updatedQueue = _playbackState.value.queue + song
        _playbackState.value = _playbackState.value.copy(queue = updatedQueue)
    }

    fun removeFromQueue(index: Int) {
        if (index !in _playbackState.value.queue.indices) return
        val updatedQueue = _playbackState.value.queue.toMutableList()
        updatedQueue.removeAt(index)
        _playbackState.value = _playbackState.value.copy(queue = updatedQueue)
    }

    fun clearQueue() {
        _playbackState.value = _playbackState.value.copy(
            queue = emptyList(),
            currentIndex = -1
        )
    }

    private fun handleTrackEnded() {
        val state = _playbackState.value
        when (state.repeatMode) {
            RepeatMode.ONE -> {
                seekTo(0)
                player?.play()
            }
            else -> playNext()
        }
    }

    fun release() {
        handler.removeCallbacks(progressUpdater)
        player?.release()
        player = null
        audioEffectManager.release()
    }
}
