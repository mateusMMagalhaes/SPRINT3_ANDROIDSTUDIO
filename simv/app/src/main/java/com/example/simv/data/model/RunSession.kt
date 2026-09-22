package com.example.simv.data.model

data class RunSession(
    val projectName: String = "",
    val location: String = "",
    val operatorId: String = "",
    val photosCaptured: Int = 0,
    val maxPhotos: Int = 20,
    val startTimeMillis: Long = 0L,
    val endTimeMillis: Long = 0L,
    val isCompleted: Boolean = false
) {
    val durationSeconds: Long
        get() = if (startTimeMillis > 0L) {
            val endTime = if (endTimeMillis > 0L) endTimeMillis else System.currentTimeMillis()
            (endTime - startTimeMillis) / 1000L
        } else 0L
}
