package com.example.simv.data.model

/**
 * Modelo de domínio que representa uma sessão de monitoramento de vegetação.
 *
 * Este modelo é usado pela camada Domain e UI. A conversão para/de [RunSessionEntity]
 * é feita pelos mappers em [com.example.simv.data.local.RunSessionMapper].
 *
 * @param id Identificador único. Zero significa sessão ainda não salva no banco.
 */
data class RunSession(
    val id: Int = 0,
    val projectName: String = "",
    val location: String = "",
    val operatorId: String = "",
    val photosCaptured: Int = 0,
    val maxPhotos: Int = 20,
    val startTimeMillis: Long = 0L,
    val endTimeMillis: Long = 0L,
    val isCompleted: Boolean = false
) {
    /** Duração em segundos. Usa [endTimeMillis] se disponível, senão o tempo atual. */
    val durationSeconds: Long
        get() = if (startTimeMillis > 0L) {
            val endTime = if (endTimeMillis > 0L) endTimeMillis else System.currentTimeMillis()
            (endTime - startTimeMillis) / 1000L
        } else 0L
}
