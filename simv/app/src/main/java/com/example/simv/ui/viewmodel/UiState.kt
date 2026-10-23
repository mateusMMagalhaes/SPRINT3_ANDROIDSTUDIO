package com.example.simv.ui.viewmodel

/**
 * Representa os três estados possíveis de uma operação assíncrona na UI.
 *
 * Uso típico no ViewModel:
 * ```
 * _saveState.value = UiState.Loading
 * try {
 *     val result = repository.save()
 *     _saveState.value = UiState.Success(result)
 * } catch (e: Exception) {
 *     _saveState.value = UiState.Error("Falha ao salvar: ${e.message}")
 * }
 * ```
 */
sealed class UiState<out T> {
    /** Operação em andamento. Exibe indicador de carregamento. */
    data object Loading : UiState<Nothing>()

    /** Operação concluída com sucesso. [data] contém o resultado. */
    data class Success<T>(val data: T) : UiState<T>()

    /** Operação falhou. [message] contém descrição amigável do erro. */
    data class Error(val message: String) : UiState<Nothing>()
}
