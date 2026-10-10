package com.example.core.audio

import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.Virtualizer
import android.util.Log
import com.example.data.model.EQPreset
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.roundToInt

data class AudioEffectState(
    val isEnabled: Boolean = true,
    val preampGainDb: Float = 0f,
    val bandGains: List<Float> = List(10) { 0f },
    val bassBoost: Int = 0, // 0 - 1000
    val virtualizer: Int = 0, // 0 - 1000
    val activePresetName: String = "Flat"
)

class AudioEffectManager {

    private val _effectState = MutableStateFlow(AudioEffectState())
    val effectState: StateFlow<AudioEffectState> = _effectState.asStateFlow()

    private var platformEqualizer: Equalizer? = null
    private var platformBassBoost: BassBoost? = null
    private var platformVirtualizer: Virtualizer? = null
    private var currentSessionId: Int = 0

    fun bindAudioSession(audioSessionId: Int) {
        if (audioSessionId <= 0 || audioSessionId == currentSessionId) return
        release()
        currentSessionId = audioSessionId

        try {
            val eq = Equalizer(0, audioSessionId)
            eq.enabled = _effectState.value.isEnabled
            platformEqualizer = eq

            val bb = BassBoost(0, audioSessionId)
            bb.enabled = _effectState.value.isEnabled
            if (bb.strengthSupported) {
                bb.setStrength(_effectState.value.bassBoost.toShort())
            }
            platformBassBoost = bb

            val virt = Virtualizer(0, audioSessionId)
            virt.enabled = _effectState.value.isEnabled
            if (virt.strengthSupported) {
                virt.setStrength(_effectState.value.virtualizer.toShort())
            }
            platformVirtualizer = virt

            applyCurrentBandsToPlatform()
        } catch (e: Exception) {
            Log.e("AudioEffectManager", "Failed to initialize platform audio effects: ${e.message}")
        }
    }

    fun setEnabled(enabled: Boolean) {
        _effectState.value = _effectState.value.copy(isEnabled = enabled)
        try {
            platformEqualizer?.enabled = enabled
            platformBassBoost?.enabled = enabled
            platformVirtualizer?.enabled = enabled
        } catch (e: Exception) {
            Log.w("AudioEffectManager", "Error toggling effects: ${e.message}")
        }
    }

    fun setPreampGain(gainDb: Float) {
        _effectState.value = _effectState.value.copy(
            preampGainDb = gainDb.coerceIn(-12f, 12f)
        )
    }

    fun setBandGain(bandIndex: Int, gainDb: Float) {
        if (bandIndex !in 0 until 10) return
        val updated = _effectState.value.bandGains.toMutableList()
        updated[bandIndex] = gainDb.coerceIn(-12f, 12f)
        _effectState.value = _effectState.value.copy(
            bandGains = updated,
            activePresetName = "Custom"
        )
        applyCurrentBandsToPlatform()
    }

    fun setBassBoost(strength: Int) {
        val clamped = strength.coerceIn(0, 1000)
        _effectState.value = _effectState.value.copy(bassBoost = clamped)
        try {
            platformBassBoost?.let {
                if (it.strengthSupported) {
                    it.setStrength(clamped.toShort())
                }
            }
        } catch (e: Exception) {
            Log.w("AudioEffectManager", "Error setting bass boost: ${e.message}")
        }
    }

    fun setVirtualizer(strength: Int) {
        val clamped = strength.coerceIn(0, 1000)
        _effectState.value = _effectState.value.copy(virtualizer = clamped)
        try {
            platformVirtualizer?.let {
                if (it.strengthSupported) {
                    it.setStrength(clamped.toShort())
                }
            }
        } catch (e: Exception) {
            Log.w("AudioEffectManager", "Error setting virtualizer: ${e.message}")
        }
    }

    fun applyPreset(preset: EQPreset) {
        _effectState.value = _effectState.value.copy(
            preampGainDb = preset.preampGainDb,
            bandGains = preset.bandGains,
            bassBoost = preset.bassBoost,
            virtualizer = preset.virtualizer,
            activePresetName = preset.name
        )
        applyCurrentBandsToPlatform()
        setBassBoost(preset.bassBoost)
        setVirtualizer(preset.virtualizer)
    }

    fun resetToFlat() {
        applyPreset(EQPreset.FLAT)
    }

    private fun applyCurrentBandsToPlatform() {
        val eq = platformEqualizer ?: return
        try {
            val numPlatformBands = eq.numberOfBands.toInt()
            val minMillibels = eq.bandLevelRange[0]
            val maxMillibels = eq.bandLevelRange[1]

            val currentGains = _effectState.value.bandGains
            for (i in 0 until minOf(numPlatformBands, currentGains.size)) {
                // Convert -12..+12 dB to millibels (-1200..+1200 mB)
                val mB = (currentGains[i] * 100f).roundToInt().toShort()
                val clamped = mB.coerceIn(minMillibels, maxMillibels)
                eq.setBandLevel(i.toShort(), clamped)
            }
        } catch (e: Exception) {
            Log.w("AudioEffectManager", "Error applying band levels: ${e.message}")
        }
    }

    fun release() {
        try {
            platformEqualizer?.release()
            platformBassBoost?.release()
            platformVirtualizer?.release()
        } catch (e: Exception) {
            Log.w("AudioEffectManager", "Error releasing effects: ${e.message}")
        } finally {
            platformEqualizer = null
            platformBassBoost = null
            platformVirtualizer = null
            currentSessionId = 0
        }
    }
}
