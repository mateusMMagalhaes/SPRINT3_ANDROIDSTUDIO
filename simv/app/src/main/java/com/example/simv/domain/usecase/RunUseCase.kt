package com.example.simv.domain.usecase

import com.example.simv.data.model.RunSession
import com.example.simv.data.repository.PreferencesRepository
import com.example.simv.data.repository.RunRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

/**
 * Caso de uso que concentra as regras de negócio do aplicativo SIMV.
 *
 * Responsabilidades:
 * - Validar entradas do formulário.
 * - Orquestrar as operações do [RunRepository].
 * - Salvar/ler preferências via [PreferencesRepository].
 * - Formatar dados para exibição na UI (tempo, etc.).
 *
 * A UI nunca acessa repositórios diretamente — tudo passa pelo UseCase.
 */
class RunUseCase(
    private val repository: RunRepository,
    private val prefsRepository: PreferencesRepository
) {
    // ── Fluxos expostos para o ViewModel ────────────────────────────────────

    val currentSession: StateFlow<RunSession> = repository.currentSession

    val allSessions: Flow<List<RunSession>> = repository.allSessions

    // ── Validação ────────────────────────────────────────────────────────────

    /**
     * Valida os campos do formulário de configuração.
     * @return `true` se todos os campos não são em branco.
     */
    fun validateSetupInput(projectName: String, location: String, operatorId: String): Boolean {
        return projectName.isNotBlank() && location.isNotBlank() && operatorId.isNotBlank()
    }

    // ── Sessão ativa ─────────────────────────────────────────────────────────

    /**
     * Inicia uma nova sessão e salva o [operatorId] nas preferências para
     * auto-preenchimento futuro.
     */
    fun startRun(projectName: String, location: String, operatorId: String, maxPhotos: Int) {
        prefsRepository.saveOperatorId(operatorId)
        repository.startNewSession(projectName, location, operatorId, maxPhotos)
    }

    /** Registra uma foto capturada. Retorna `true` se a sessão foi concluída. */
    fun capturePhoto(): Boolean = repository.capturePhoto()

    /** Encerra a sessão manualmente antes de atingir o limite de fotos. */
    fun stopRun() = repository.stopRun()

    /**
     * Persiste a sessão atual no banco de dados.
     * Deve ser chamada dentro de uma coroutine.
     */
    suspend fun saveCompletedSession(): Long = repository.saveCurrentSession()

    /**
     * Deleta uma sessão do histórico.
     * Deve ser chamada dentro de uma coroutine.
     */
    suspend fun deleteSession(session: RunSession) = repository.deleteSession(session)

    /** Limpa o estado da sessão ativa em memória. */
    fun resetRun() = repository.resetSession()

    // ── Preferências ─────────────────────────────────────────────────────────

    /** Retorna o último ID de operador salvo (para auto-preenchimento). */
    fun getLastOperatorId(): String = prefsRepository.getLastOperatorId()

    /** Retorna o número máximo de fotos configurado. */
    fun getMaxPhotos(): Int = prefsRepository.getMaxPhotos()

    /** Salva o número máximo de fotos nas preferências. */
    fun saveMaxPhotos(count: Int) = prefsRepository.saveMaxPhotos(count)

    // ── Formatação ───────────────────────────────────────────────────────────

    /**
     * Formata uma duração em segundos para o formato `HH:mm:ss`.
     */
    fun formatDuration(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, secs)
    }

    /**
     * Formata um timestamp em milissegundos para uma data legível (dd/MM/yyyy HH:mm).
     */
    fun formatTimestamp(millis: Long): String {
        if (millis == 0L) return "--"
        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy  HH:mm", Locale.getDefault())
        return sdf.format(java.util.Date(millis))
    }
}
