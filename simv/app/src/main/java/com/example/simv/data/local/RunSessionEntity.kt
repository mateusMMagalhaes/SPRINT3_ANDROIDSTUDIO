package com.example.simv.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidade Room que representa uma sessão de monitoramento salva no banco de dados.
 * Cada linha na tabela "run_sessions" corresponde a uma [RunSessionEntity].
 */
@Entity(tableName = "run_sessions")
data class RunSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val projectName: String,
    val location: String,
    val operatorId: String,
    val photosCaptured: Int,
    val maxPhotos: Int,
    val startTimeMillis: Long,
    val endTimeMillis: Long,
    val isCompleted: Boolean
)
