package com.example.data.model

data class EQPreset(
    val id: String,
    val name: String,
    val isBuiltIn: Boolean = true,
    val preampGainDb: Float = 0f,
    // 10 bands gains in dB (-12f to +12f)
    val bandGains: List<Float> = List(10) { 0f },
    val bassBoost: Int = 0, // 0 - 1000
    val virtualizer: Int = 0 // 0 - 1000
) {
    companion object {
        val FREQUENCY_LABELS = listOf(
            "31 Hz", "62 Hz", "125 Hz", "250 Hz", "500 Hz",
            "1 kHz", "2 kHz", "4 kHz", "8 kHz", "16 kHz"
        )

        val FLAT = EQPreset(
            id = "flat",
            name = "Flat",
            bandGains = listOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
        )

        val BASS_BOOST = EQPreset(
            id = "bass_boost",
            name = "Bass Boost",
            preampGainDb = -1.5f,
            bandGains = listOf(6f, 5.5f, 4f, 2f, 0f, 0f, 0f, 0f, 0.5f, 1f),
            bassBoost = 600
        )

        val TREBLE_BOOST = EQPreset(
            id = "treble_boost",
            name = "Treble Boost",
            preampGainDb = -1f,
            bandGains = listOf(-1f, -1f, 0f, 0f, 1f, 2.5f, 4.5f, 6f, 7f, 7.5f)
        )

        val VOCAL = EQPreset(
            id = "vocal",
            name = "Vocal",
            bandGains = listOf(-2f, -1.5f, 0f, 2f, 4f, 4.5f, 3f, 1.5f, 0f, -1f)
        )

        val ROCK = EQPreset(
            id = "rock",
            name = "Rock",
            preampGainDb = -1f,
            bandGains = listOf(4.5f, 3.5f, 2f, -1f, -1.5f, 0.5f, 2.5f, 4f, 4.5f, 5f),
            bassBoost = 300
        )

        val POP = EQPreset(
            id = "pop",
            name = "Pop",
            bandGains = listOf(-1f, 1f, 3f, 4f, 3.5f, 1.5f, 0f, 1f, 2f, 2.5f)
        )

        val CLASSICAL = EQPreset(
            id = "classical",
            name = "Classical",
            bandGains = listOf(3.5f, 3f, 2.5f, 1f, -1f, -1f, 0f, 2f, 3f, 3.5f)
        )

        val JAZZ = EQPreset(
            id = "jazz",
            name = "Jazz",
            bandGains = listOf(2.5f, 2f, 1f, 2f, -1f, -1f, 0f, 1.5f, 2.5f, 3f)
        )

        val ELECTRONIC = EQPreset(
            id = "electronic",
            name = "Electronic",
            preampGainDb = -2f,
            bandGains = listOf(5.5f, 5f, 2f, 0f, -1.5f, 1.5f, 0.5f, 2f, 4.5f, 5f),
            bassBoost = 500,
            virtualizer = 250
        )

        val ACOUSTIC = EQPreset(
            id = "acoustic",
            name = "Acoustic",
            bandGains = listOf(3f, 2.5f, 1f, 0.5f, 1.5f, 1.5f, 2.5f, 3f, 2.5f, 2f)
        )

        val DEFAULT_PRESETS = listOf(
            FLAT, BASS_BOOST, TREBLE_BOOST, VOCAL, ROCK, POP, CLASSICAL, JAZZ, ELECTRONIC, ACOUSTIC
        )
    }
}
