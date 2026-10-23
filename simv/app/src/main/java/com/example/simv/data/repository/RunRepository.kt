package com.example.simv.data.repository

import com.example.simv.data.local.RunSessionDao
import com.example.simv.data.local.RunSessionMapper
import com.example.simv.data.local.toDomain
import com.example.simv.data.local.toEntity
import com.example.simv.data.model.RunSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

/**
 * Repositório principal do aplicativo.
 *
 * Responsabilidades:
 * - Gerenciar o estado da sessão ativa em memória ([_currentSession]).
 * - Persistir sessões concluídas no banco de dados via [RunSessionDao].
 * - Expor o histórico de todas as sessões como um [Flow] reativo.
 *
 * A UI e o Domain nunca acessam o DAO diretamente; tudo passa por aqui.
 */
class RunRepository(private val dao: RunSessionDao) {

    // ── Sessão ativa (em memória) ────────────────────────────────────────────

    private val _currentSession = MutableStateFlow(RunSession())
    val currentSession: StateFlow<RunSession> = _currentSession.asStateFlow()

    // ── Histórico persistido (Flow reativo do Room) ──────────────────────────

    /**
     * Emite a lista de todas as sessões do banco toda vez que ela muda.
     * Ordenado da mais recente para a mais antiga.
     */
    val allSessions: Flow<List<RunSession>> = dao.getAllSessions().map { entities ->
        entities.map { it.toDomain() }
    }

    // ── Operações de sessão ativa ────────────────────────────────────────────

    /**
     * Inicializa uma nova sessão em memória com os dados do formulário.
     * Não persiste ainda — a sessão só vai para o banco ao ser concluída.
     */
    fun startNewSession(projectName: String, location: String, operatorId: String, maxPhotos: Int) {
        _currentSession.value = RunSession(
            projectName = projectName.trim(),
            location = location.trim(),
            operatorId = operatorId.trim(),
            maxPhotos = maxPhotos,
            photosCaptured = 0,
            startTimeMillis = System.currentTimeMillis()
        )
    }

    /**
     * Incrementa o contador de fotos. Retorna `true` se a sessão foi concluída
     * (atingiu o limite máximo de fotos).
     */
    fun capturePhoto(): Boolean {
        val session = _currentSession.value
        if (session.isCompleted || session.photosCaptured >= session.maxPhotos) return false

        val newCount = session.photosCaptured + 1
        val isDone = newCount >= session.maxPhotos

        _currentSession.value = session.copy(
            photosCaptured = newCount,
            isCompleted = isDone,
            endTimeMillis = if (isDone) System.currentTimeMillis() else session.endTimeMillis
        )
        return isDone
    }

    /**
     * Encerra a sessão manualmente (antes de atingir o limite).
     */
    fun stopRun() {
        _currentSession.value = _currentSession.value.copy(
            isCompleted = true,
            endTimeMillis = System.currentTimeMillis()
        )
    }

    /**
     * Persiste a sessão atual no banco de dados Room.
     * Deve ser chamada dentro de uma coroutine (suspend).
     * Retorna o ID gerado pelo banco.
     */
    suspend fun saveCurrentSession(): Long {
        val entity = _currentSession.value.toEntity()
        return dao.insert(entity)
    }

    /**
     * Deleta uma sessão do histórico pelo seu ID.
     */
    suspend fun deleteSession(session: RunSession) {
        dao.delete(session.toEntity())
    }

    /**
     * Limpa a sessão ativa da memória, preparando o app para uma nova corrida.
     */
    fun resetSession() {
        _currentSession.value = RunSession()
    }
}
