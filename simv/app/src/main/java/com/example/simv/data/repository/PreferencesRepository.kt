package com.example.simv.data.repository

import com.example.simv.data.local.UserPreferences

/**
 * Repositório para dados simples de configuração/preferência do usuário.
 * Delega para [UserPreferences] (SharedPreferences), mas expõe uma interface
 * limpa para a camada Domain.
 */
class PreferencesRepository(private val prefs: UserPreferences) {

    /** Último ID de operador salvo. Retorna string vazia se nunca foi salvo. */
    fun getLastOperatorId(): String = prefs.lastOperatorId

    /** Salva o ID do operador para auto-preenchimento na próxima sessão. */
    fun saveOperatorId(id: String) {
        prefs.lastOperatorId = id
    }

    /** Retorna o número máximo de fotos configurado (padrão: 20). */
    fun getMaxPhotos(): Int = prefs.maxPhotos

    /** Salva o número máximo de fotos escolhido pelo usuário. */
    fun saveMaxPhotos(count: Int) {
        prefs.maxPhotos = count
    }
}
