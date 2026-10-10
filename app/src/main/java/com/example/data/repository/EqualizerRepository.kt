package com.example.data.repository

import com.example.core.database.dao.EQPresetDao
import com.example.core.database.entity.EQPresetEntity
import com.example.data.model.EQPreset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class EqualizerRepository(private val presetDao: EQPresetDao) {

    val allPresets: Flow<List<EQPreset>> = presetDao.getAllPresets().map { entities ->
        if (entities.isEmpty()) {
            EQPreset.DEFAULT_PRESETS
        } else {
            entities.map { it.toEQPreset() }
        }
    }

    suspend fun saveCustomPreset(preset: EQPreset) = withContext(Dispatchers.IO) {
        val entity = EQPresetEntity.fromEQPreset(preset.copy(isBuiltIn = false))
        presetDao.insertPreset(entity)
    }

    suspend fun deleteCustomPreset(presetId: String) = withContext(Dispatchers.IO) {
        presetDao.deleteCustomPreset(presetId)
    }
}
