package com.example.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.EQPreset

@Entity(tableName = "eq_presets")
data class EQPresetEntity(
    @PrimaryKey val id: String,
    val name: String,
    val isBuiltIn: Boolean,
    val preampGainDb: Float,
    val bandGainsCommaSeparated: String,
    val bassBoost: Int,
    val virtualizer: Int
) {
    fun toEQPreset(): EQPreset {
        val gains = bandGainsCommaSeparated.split(",").mapNotNull { it.trim().toFloatOrNull() }
        val finalGains = if (gains.size == 10) gains else List(10) { 0f }
        return EQPreset(
            id = id,
            name = name,
            isBuiltIn = isBuiltIn,
            preampGainDb = preampGainDb,
            bandGains = finalGains,
            bassBoost = bassBoost,
            virtualizer = virtualizer
        )
    }

    companion object {
        fun fromEQPreset(preset: EQPreset): EQPresetEntity = EQPresetEntity(
            id = preset.id,
            name = preset.name,
            isBuiltIn = preset.isBuiltIn,
            preampGainDb = preset.preampGainDb,
            bandGainsCommaSeparated = preset.bandGains.joinToString(","),
            bassBoost = preset.bassBoost,
            virtualizer = preset.virtualizer
        )
    }
}
