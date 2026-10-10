package com.example.ui.screens.equalizer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.audio.AudioEffectManager
import com.example.core.audio.AudioEffectState
import com.example.data.model.EQPreset
import com.example.data.repository.EqualizerRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EqualizerViewModel(
    val audioEffectManager: AudioEffectManager,
    private val equalizerRepository: EqualizerRepository
) : ViewModel() {

    val effectState: StateFlow<AudioEffectState> = audioEffectManager.effectState

    val presets: StateFlow<List<EQPreset>> = equalizerRepository.allPresets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), EQPreset.DEFAULT_PRESETS)

    fun setEnabled(enabled: Boolean) {
        audioEffectManager.setEnabled(enabled)
    }

    fun setPreamp(gainDb: Float) {
        audioEffectManager.setPreampGain(gainDb)
    }

    fun setBandGain(bandIndex: Int, gainDb: Float) {
        audioEffectManager.setBandGain(bandIndex, gainDb)
    }

    fun setBassBoost(strength: Int) {
        audioEffectManager.setBassBoost(strength)
    }

    fun setVirtualizer(strength: Int) {
        audioEffectManager.setVirtualizer(strength)
    }

    fun selectPreset(preset: EQPreset) {
        audioEffectManager.applyPreset(preset)
    }

    fun resetToFlat() {
        audioEffectManager.resetToFlat()
    }

    fun saveCustomPreset(name: String) {
        viewModelScope.launch {
            val currentState = effectState.value
            val newPreset = EQPreset(
                id = "custom_${System.currentTimeMillis()}",
                name = name,
                isBuiltIn = false,
                preampGainDb = currentState.preampGainDb,
                bandGains = currentState.bandGains,
                bassBoost = currentState.bassBoost,
                virtualizer = currentState.virtualizer
            )
            equalizerRepository.saveCustomPreset(newPreset)
            audioEffectManager.applyPreset(newPreset)
        }
    }
}
