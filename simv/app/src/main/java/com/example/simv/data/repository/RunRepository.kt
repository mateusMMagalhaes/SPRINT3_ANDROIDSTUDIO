package com.example.simv.data.repository

import com.example.simv.data.model.RunSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RunRepository {
    private val _currentSession = MutableStateFlow(RunSession())
    val currentSession: StateFlow<RunSession> = _currentSession.asStateFlow()

    fun startNewSession(projectName: String, location: String, operatorId: String) {
        _currentSession.value = RunSession(
            projectName = projectName.trim(),
            location = location.trim(),
            operatorId = operatorId.trim(),
            photosCaptured = 0,
            startTimeMillis = System.currentTimeMillis()
        )
    }

    fun capturePhoto(): Boolean {
        val session = _currentSession.value
        if (session.isCompleted || session.photosCaptured >= session.maxPhotos) {
            return false
        }
        val newCount = session.photosCaptured + 1
        val isDone = newCount >= session.maxPhotos
        val endTime = if (isDone) System.currentTimeMillis() else 0L

        _currentSession.value = session.copy(
            photosCaptured = newCount,
            isCompleted = isDone,
            endTimeMillis = if (isDone) endTime else session.endTimeMillis
        )
        return isDone
    }

    fun stopRun() {
        val session = _currentSession.value
        _currentSession.value = session.copy(
            isCompleted = true,
            endTimeMillis = System.currentTimeMillis()
        )
    }

    fun resetSession() {
        _currentSession.value = RunSession()
    }
}
