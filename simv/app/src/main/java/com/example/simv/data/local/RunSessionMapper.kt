package com.example.simv.data.local

import com.example.simv.data.model.RunSession

/**
 * Funções de mapeamento entre a entidade de banco de dados [RunSessionEntity]
 * e o modelo de domínio [RunSession].
 *
 * Mantém o isolamento entre a camada Data e a camada Domain:
 * a UI e o Domain nunca precisam conhecer [RunSessionEntity].
 */

fun RunSessionEntity.toDomain(): RunSession = RunSession(
    id = this.id,
    projectName = this.projectName,
    location = this.location,
    operatorId = this.operatorId,
    photosCaptured = this.photosCaptured,
    maxPhotos = this.maxPhotos,
    startTimeMillis = this.startTimeMillis,
    endTimeMillis = this.endTimeMillis,
    isCompleted = this.isCompleted
)

fun RunSession.toEntity(): RunSessionEntity = RunSessionEntity(
    id = this.id,
    projectName = this.projectName,
    location = this.location,
    operatorId = this.operatorId,
    photosCaptured = this.photosCaptured,
    maxPhotos = this.maxPhotos,
    startTimeMillis = this.startTimeMillis,
    endTimeMillis = this.endTimeMillis,
    isCompleted = this.isCompleted
)
