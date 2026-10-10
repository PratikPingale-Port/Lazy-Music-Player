package com.example.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.core.database.entity.EQPresetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EQPresetDao {
    @Query("SELECT * FROM eq_presets ORDER BY name ASC")
    fun getAllPresets(): Flow<List<EQPresetEntity>>

    @Query("SELECT * FROM eq_presets WHERE id = :id LIMIT 1")
    suspend fun getPresetById(id: String): EQPresetEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreset(preset: EQPresetEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPresets(presets: List<EQPresetEntity>)

    @Query("DELETE FROM eq_presets WHERE id = :id AND isBuiltIn = 0")
    suspend fun deleteCustomPreset(id: String)
}
