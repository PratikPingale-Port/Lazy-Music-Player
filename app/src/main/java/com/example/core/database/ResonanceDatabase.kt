package com.example.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.core.database.dao.EQPresetDao
import com.example.core.database.dao.PlaylistDao
import com.example.core.database.dao.SongDao
import com.example.core.database.entity.EQPresetEntity
import com.example.core.database.entity.PlaylistEntity
import com.example.core.database.entity.PlaylistSongCrossRef
import com.example.core.database.entity.SongEntity
import com.example.data.model.EQPreset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        SongEntity::class,
        PlaylistEntity::class,
        PlaylistSongCrossRef::class,
        EQPresetEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ResonanceDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun eqPresetDao(): EQPresetDao

    companion object {
        @Volatile
        private var INSTANCE: ResonanceDatabase? = null

        fun getInstance(context: Context): ResonanceDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ResonanceDatabase::class.java,
                    "resonance_music.db"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Prepopulate default audio presets
                        CoroutineScope(Dispatchers.IO).launch {
                            val presetDao = getInstance(context).eqPresetDao()
                            val entities = EQPreset.DEFAULT_PRESETS.map {
                                EQPresetEntity.fromEQPreset(it)
                            }
                            presetDao.insertPresets(entities)
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
