package com.example.simv.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simv.data.model.RunSession
import com.example.simv.ui.theme.SimvBorder
import com.example.simv.ui.theme.SimvCyan
import com.example.simv.ui.theme.SimvDarkBg
import com.example.simv.ui.theme.SimvDarkButtonBg
import com.example.simv.ui.theme.SimvError
import com.example.simv.ui.theme.SimvInputFieldBg
import com.example.simv.ui.theme.SimvTextPrimary
import com.example.simv.ui.theme.SimvTextSecondary
import com.example.simv.ui.viewmodel.UiState

/**
 * Tela de Histórico de Sessões.
 *
 * Exibe todos os registros de sessões salvas no banco de dados Room.
 * Gerencia três estados visuais distintos:
 * - [UiState.Loading]: indicador de progresso circular
 * - [UiState.Success] com lista vazia: mensagem de estado vazio
 * - [UiState.Success] com dados: lista de cards de sessão
 * - [UiState.Error]: mensagem de erro amigável
 */
@Composable
fun HistoryScreen(
    historyState: UiState<List<RunSession>>,
    onBackClicked: () -> Unit,
    onDeleteSession: (RunSession) -> Unit,
    formatTimestamp: (Long) -> String,
    formatDuration: (Long) -> String,
    modifier: Modifier = Modifier
) {
    var sessionToDelete by remember { mutableStateOf<RunSession?>(null) }

    // Diálogo de confirmação de exclusão
    sessionToDelete?.let { session ->
        DeleteConfirmationDialog(
            sessionName = session.projectName,
            onConfirm = {
                onDeleteSession(session)
                sessionToDelete = null
            },
            onDismiss = { sessionToDelete = null }
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SimvDarkBg)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Header ──────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onBackClicked,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.height(36.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SimvTextSecondary)
                ) {
                    Text("← Voltar", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "Histórico",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = SimvTextPrimary,
                    letterSpacing = 0.5.sp
                )
            }

            HorizontalDivider(color = SimvBorder.copy(alpha = 0.4f), thickness = 1.dp)

            // ── Conteúdo por estado ─────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                when (historyState) {
                    is UiState.Loading -> {
                        LoadingContent()
                    }

                    is UiState.Error -> {
                        ErrorContent(message = historyState.message)
                    }

                    is UiState.Success -> {
                        val sessions = historyState.data
                        if (sessions.isEmpty()) {
                            EmptyContent()
                        } else {
                            SessionList(
                                sessions = sessions,
                                onDeleteClicked = { sessionToDelete = it },
                                formatTimestamp = formatTimestamp,
                                formatDuration = formatDuration
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Sub-componentes ──────────────────────────────────────────────────────────

@Composable
private fun LoadingContent() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CircularProgressIndicator(color = SimvCyan, strokeWidth = 3.dp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Carregando histórico...",
            color = SimvTextSecondary,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun ErrorContent(message: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(24.dp)
    ) {
        Text(
            text = "⚠",
            fontSize = 40.sp,
            color = SimvError
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = message,
            color = SimvError,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun EmptyContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(24.dp)
    ) {
        Text(text = "📂", fontSize = 48.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Nenhuma sessão encontrada",
            color = SimvTextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Inicie e conclua uma sessão de monitoramento\npara que ela apareça aqui.",
            color = SimvTextSecondary,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )
    }
}

@Composable
private fun SessionList(
    sessions: List<RunSession>,
    onDeleteClicked: (RunSession) -> Unit,
    formatTimestamp: (Long) -> String,
    formatDuration: (Long) -> String
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 16.dp)
    ) {
        item {
            Text(
                text = "${sessions.size} sessão(ões) registrada(s)",
                color = SimvTextSecondary,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
            )
        }
        items(sessions, key = { it.id }) { session ->
            SessionCard(
                session = session,
                onDeleteClicked = { onDeleteClicked(session) },
                formatTimestamp = formatTimestamp,
                formatDuration = formatDuration
            )
        }
    }
}

@Composable
private fun SessionCard(
    session: RunSession,
    onDeleteClicked: () -> Unit,
    formatTimestamp: (Long) -> String,
    formatDuration: (Long) -> String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SimvInputFieldBg)
            .border(1.dp, SimvBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        // Cabeçalho do card: nome do projeto + badge de status
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = session.projectName,
                color = SimvTextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Badge de concluída/interrompida
            val (badgeColor, badgeText) = if (session.isCompleted) {
                SimvCyan to "Concluída"
            } else {
                SimvError.copy(alpha = 0.8f) to "Interrompida"
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(badgeColor.copy(alpha = 0.15f))
                    .border(1.dp, badgeColor.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 3.dp)
            ) {
                Text(
                    text = badgeText,
                    color = badgeColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider(color = SimvBorder.copy(alpha = 0.3f))
        Spacer(modifier = Modifier.height(10.dp))

        // Detalhes da sessão
        SessionDetail(label = "Operador", value = session.operatorId)
        Spacer(modifier = Modifier.height(4.dp))
        SessionDetail(label = "Local", value = session.location)
        Spacer(modifier = Modifier.height(4.dp))
        SessionDetail(label = "Data / Hora", value = formatTimestamp(session.startTimeMillis))
        Spacer(modifier = Modifier.height(4.dp))
        SessionDetail(
            label = "Fotos",
            value = "${session.photosCaptured} / ${session.maxPhotos}"
        )
        Spacer(modifier = Modifier.height(4.dp))
        SessionDetail(
            label = "Duração",
            value = formatDuration(session.durationSeconds)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Botão de deletar
        OutlinedButton(
            onClick = onDeleteClicked,
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = SimvError)
        ) {
            Text("Deletar sessão", fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun SessionDetail(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = SimvTextSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            color = SimvTextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun DeleteConfirmationDialog(
    sessionName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SimvInputFieldBg,
        titleContentColor = SimvTextPrimary,
        textContentColor = SimvTextSecondary,
        title = {
            Text("Deletar sessão?", fontWeight = FontWeight.Bold)
        },
        text = {
            Text(
                "A sessão \"$sessionName\" será removida permanentemente do histórico.",
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = SimvError)
            ) {
                Text("Deletar", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = SimvTextSecondary)
            }
        }
    )
}
