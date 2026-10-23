package com.example.simv.navigation

/**
 * Enum que representa todas as telas do aplicativo SIMV.
 *
 * Centralizado no pacote [navigation] para que nenhuma tela ou ViewModel
 * precise importar definições de tela de outro lugar.
 */
enum class SimvScreen {
    /** Tela inicial com botão de início e acesso ao histórico. */
    START,

    /** Formulário de configuração da sessão (projeto, localização, operador). */
    SETUP,

    /** Visualização da câmera em tempo real com captura de fotos. */
    CAMERA_VIEW,

    /** Tela de resumo exibida ao concluir ou encerrar uma sessão. */
    COMPLETED,

    /** Histórico de todas as sessões salvas no banco de dados. */
    HISTORY
}
