package com.example.simv.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Banco de dados Room do aplicativo SIMV.
 *
 * Acesse-o sempre via [SimvDatabase.getInstance] para garantir que só
 * exista uma instância (padrão Singleton thread-safe com double-checked locking).
 */
@Database(
    entities = [RunSessionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SimvDatabase : RoomDatabase() {

    abstract fun runSessionDao(): RunSessionDao

    companion object {
        @Volatile
        private var INSTANCE: SimvDatabase? = null

        fun getInstance(context: Context): SimvDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    SimvDatabase::class.java,
                    "simv_database"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
