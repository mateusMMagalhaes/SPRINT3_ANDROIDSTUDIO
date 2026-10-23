package com.example.simv.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.simv.data.local.SimvDatabase
import com.example.simv.data.local.UserPreferences
import com.example.simv.data.model.RunSession
import com.example.simv.data.repository.PreferencesRepository
import com.example.simv.data.repository.RunRepository
import com.example.simv.domain.usecase.RunUseCase
import com.example.simv.navigation.SimvScreen
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Estado do formulário de configuração de sessão.
 *
 * Mantido no ViewModel para sobreviver a recomposições; campos individuais
 * são atualizados com [copy] para disparar recomposição apenas onde necessário.
 */
data class SetupFormState(
    val projectName: String = "",
    val location: String = "",
    val operatorId: String = "",
    val maxPhotos: Int = 20,
    val errorMessage: String? = null
)

/**
 * ViewModel principal do aplicativo SIMV.
 *
 * Usa [AndroidViewModel] para ter acesso ao [Application] Context,
 * necessário para instanciar o banco de dados Room e as SharedPreferences
 * sem vazar o contexto da Activity.
 *
 * Responsabilidades:
 * - Gerenciar a navegação entre telas ([currentScreen]).
 * - Expor estados reeativos para a UI ([setupForm], [currentSession], [historyState], [saveState]).
 * - Orquestrar operações de negócio via [useCase].
 * - Controlar o cronômetro da sessão ativa.
 */
class RunViewModel(application: Application) : AndroidViewModel(application) {

    // ── Inicialização das dependências ───────────────────────────────────────

    private val database = SimvDatabase.getInstance(application)
    private val userPrefs = UserPreferences(application)
    private val runRepository = RunRepository(database.runSessionDao())
    private val prefsRepository = PreferencesRepository(userPrefs)
    val useCase = RunUseCase(runRepository, prefsRepository)

    // ── Estado de navegação ──────────────────────────────────────────────────

    private val _currentScreen = MutableStateFlow(SimvScreen.START)
    val currentScreen: StateFlow<SimvScreen> = _currentScreen.asStateFlow()

    // ── Estado do formulário ─────────────────────────────────────────────────

    private val _setupForm = MutableStateFlow(
        SetupFormState(
            operatorId = useCase.getLastOperatorId(),
            maxPhotos = useCase.getMaxPhotos()
        )
    )
    val setupForm: StateFlow<SetupFormState> = _setupForm.asStateFlow()

    // ── Estado da sessão ativa ───────────────────────────────────────────────

    val currentSession: StateFlow<RunSession> = useCase.currentSession

    // ── Estado do histórico ──────────────────────────────────────────────────

    private val _historyState = MutableStateFlow<UiState<List<RunSession>>>(UiState.Loading)
    val historyState: StateFlow<UiState<List<RunSession>>> = _historyState.asStateFlow()

    // ── Estado de salvar sessão ──────────────────────────────────────────────

    private val _saveState = MutableStateFlow<UiState<Unit>>(UiState.Success(Unit))
    val saveState: StateFlow<UiState<Unit>> = _saveState.asStateFlow()

    // ── Cronômetro ───────────────────────────────────────────────────────────

    private val _elapsedSeconds = MutableStateFlow(0L)
    val elapsedSeconds: StateFlow<Long> = _elapsedSeconds.asStateFlow()

    private val _flashTrigger = MutableStateFlow(false)
    val flashTrigger: StateFlow<Boolean> = _flashTrigger.asStateFlow()

    private var timerJob: Job? = null

    // ── Carregamento do histórico ────────────────────────────────────────────

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            _historyState.value = UiState.Loading
            try {
                useCase.allSessions.collect { sessions ->
                    _historyState.value = UiState.Success(sessions)
                }
            } catch (e: Exception) {
                _historyState.value = UiState.Error("Erro ao carregar histórico: ${e.message}")
            }
        }
    }

    // ── Handlers de eventos da UI ────────────────────────────────────────────

    fun onStartRunClicked() {
        _currentScreen.value = SimvScreen.SETUP
    }

    fun onHistoryClicked() {
        _currentScreen.value = SimvScreen.HISTORY
    }

    fun onBackFromHistoryClicked() {
        _currentScreen.value = SimvScreen.START
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

    fun onMaxPhotosChanged(value: Int) {
        val clamped = value.coerceIn(1, 100)
        _setupForm.update { it.copy(maxPhotos = clamped) }
        useCase.saveMaxPhotos(clamped)
    }

    fun onSetupNextClicked() {
        val form = _setupForm.value
        val isValid = useCase.validateSetupInput(
            projectName = form.projectName,
            location = form.location,
            operatorId = form.operatorId
        )

        if (!isValid) {
            _setupForm.update { it.copy(errorMessage = "Preencha todos os campos para continuar.") }
            return
        }

        useCase.startRun(
            projectName = form.projectName,
            location = form.location,
            operatorId = form.operatorId,
            maxPhotos = form.maxPhotos
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
            saveSessionAndNavigate()
        }
    }

    fun onStopRunClicked() {
        useCase.stopRun()
        stopTimer()
        saveSessionAndNavigate()
    }

    fun onDoneClicked() {
        stopTimer()
        useCase.resetRun()
        _setupForm.update { current ->
            SetupFormState(
                operatorId = useCase.getLastOperatorId(),
                maxPhotos = useCase.getMaxPhotos()
            )
        }
        _elapsedSeconds.value = 0L
        _saveState.value = UiState.Success(Unit)
        _currentScreen.value = SimvScreen.START
    }

    fun onDeleteSession(session: RunSession) {
        viewModelScope.launch {
            try {
                useCase.deleteSession(session)
            } catch (e: Exception) {
                _historyState.value = UiState.Error("Erro ao deletar sessão: ${e.message}")
            }
        }
    }

    // ── Persistência ─────────────────────────────────────────────────────────

    /**
     * Salva a sessão concluída no banco e navega para a tela de conclusão.
     * Exibe estado de Loading enquanto salva e trata erros sem crashar o app.
     */
    private fun saveSessionAndNavigate() {
        viewModelScope.launch {
            _saveState.value = UiState.Loading
            try {
                useCase.saveCompletedSession()
                _saveState.value = UiState.Success(Unit)
            } catch (e: Exception) {
                _saveState.value = UiState.Error("Erro ao salvar sessão: ${e.message}")
            } finally {
                _currentScreen.value = SimvScreen.COMPLETED
            }
        }
    }

    // ── Cronômetro ───────────────────────────────────────────────────────────

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

    fun getFormattedTime(): String = useCase.formatDuration(_elapsedSeconds.value)

    fun formatTimestamp(millis: Long): String = useCase.formatTimestamp(millis)

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}
