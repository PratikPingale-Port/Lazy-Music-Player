package com.example

import com.example.data.model.EQPreset
import com.example.data.model.Song
import com.example.playback.controller.PlaybackState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun songDurationFormatting_isCorrect() {
        val song = Song(
            id = "test_1",
            title = "Test Track",
            artist = "Audiophile",
            album = "Acoustics",
            durationMs = 195000L, // 3 minutes 15 seconds
            uri = "content://media/audio/1"
        )
        assertEquals("3:15", song.formattedDuration)
    }

    @Test
    fun eqPreset_hasTenBands() {
        assertEquals(10, EQPreset.FLAT.bandGains.size)
        assertEquals(10, EQPreset.BASS_BOOST.bandGains.size)
        assertEquals(10, EQPreset.FREQUENCY_LABELS.size)
    }

    @Test
    fun playbackState_progressCalculation() {
        val state = PlaybackState(
            currentPositionMs = 50000L,
            durationMs = 100000L
        )
        assertEquals(0.5f, state.progress, 0.001f)
        assertEquals("0:50", state.formattedPosition)
        assertEquals("1:40", state.formattedDuration)
    }
}
