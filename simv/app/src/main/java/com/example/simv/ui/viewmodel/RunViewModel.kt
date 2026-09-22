package com.example.simv.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simv.data.model.RunSession
import com.example.simv.data.repository.RunRepository
import com.example.simv.domain.usecase.RunUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class SimvScreen {
    START,
    SETUP,
    CAMERA_VIEW,
    COMPLETED
}

data class SetupFormState(
    val projectName: String = "",
    val location: String = "",
    val operatorId: String = "",
    val errorMessage: String? = null
)

class RunViewModel(
    val useCase: RunUseCase = RunUseCase(RunRepository())
) : ViewModel() {

    private val _currentScreen = MutableStateFlow(SimvScreen.START)
    val currentScreen: StateFlow<SimvScreen> = _currentScreen.asStateFlow()

    private val _setupForm = MutableStateFlow(SetupFormState())
    val setupForm: StateFlow<SetupFormState> = _setupForm.asStateFlow()

    val currentSession: StateFlow<RunSession> = useCase.currentSession

    private val _elapsedSeconds = MutableStateFlow(0L)
    val elapsedSeconds: StateFlow<Long> = _elapsedSeconds.asStateFlow()

    private val _flashTrigger = MutableStateFlow(false)
    val flashTrigger: StateFlow<Boolean> = _flashTrigger.asStateFlow()

    private var timerJob: Job? = null

    fun onStartRunClicked() {
        _currentScreen.value = SimvScreen.SETUP
    }

    fun onProjectNameChanged(value: String) {
        _setupForm.update { it.copy(projectName = value, errorMessage = null) }
    }

    fun onLocationChanged(value: String) {
        _setupForm.update { it.copy(location = value, errorMessage = null) }
    }

    fun onOperatorIdChanged(value: String) {
        _setupForm.update { it.copy(operatorId = value, errorMessage = null) }
    }

    fun onSetupNextClicked() {
        val form = _setupForm.value
        val isValid = useCase.validateSetupInput(
            projectName = form.projectName,
            location = form.location,
            operatorId = form.operatorId
        )

        if (!isValid) {
            _setupForm.update { it.copy(errorMessage = "Please fill in all fields to continue.") }
            return
        }

        useCase.startRun(
            projectName = form.projectName,
            location = form.location,
            operatorId = form.operatorId
        )
        _elapsedSeconds.value = 0L
        _currentScreen.value = SimvScreen.CAMERA_VIEW
        startTimer()
    }

    fun onTakePhotoClicked() {
        if (_currentScreen.value != SimvScreen.CAMERA_VIEW) return

        triggerFlashEffect()

        val isCompleted = useCase.capturePhoto()
        if (isCompleted) {
            stopTimer()
            _currentScreen.value = SimvScreen.COMPLETED
        }
    }

    fun onStopRunClicked() {
        useCase.stopRun()
        stopTimer()
        _currentScreen.value = SimvScreen.COMPLETED
    }

    fun onDoneClicked() {
        stopTimer()
        useCase.resetRun()
        _setupForm.value = SetupFormState()
        _elapsedSeconds.value = 0L
        _currentScreen.value = SimvScreen.START
    }

    private fun triggerFlashEffect() {
        viewModelScope.launch {
            _flashTrigger.value = true
            delay(120)
            _flashTrigger.value = false
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            while (_currentScreen.value == SimvScreen.CAMERA_VIEW) {
                delay(500)
                _elapsedSeconds.value = (System.currentTimeMillis() - startTime) / 1000L
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    fun getFormattedTime(): String {
        return useCase.formatDuration(_elapsedSeconds.value)
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}
