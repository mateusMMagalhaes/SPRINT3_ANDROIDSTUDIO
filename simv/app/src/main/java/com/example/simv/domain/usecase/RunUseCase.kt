package com.example.simv.domain.usecase

import com.example.simv.data.model.RunSession
import com.example.simv.data.repository.RunRepository
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

class RunUseCase(private val repository: RunRepository) {

    val currentSession: StateFlow<RunSession> = repository.currentSession

    fun validateSetupInput(projectName: String, location: String, operatorId: String): Boolean {
        return projectName.isNotBlank() && location.isNotBlank() && operatorId.isNotBlank()
    }

    fun startRun(projectName: String, location: String, operatorId: String) {
        repository.startNewSession(projectName, location, operatorId)
    }

    fun capturePhoto(): Boolean {
        return repository.capturePhoto()
    }

    fun stopRun() {
        repository.stopRun()
    }

    fun resetRun() {
        repository.resetSession()
    }

    fun formatDuration(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, secs)
    }
}
