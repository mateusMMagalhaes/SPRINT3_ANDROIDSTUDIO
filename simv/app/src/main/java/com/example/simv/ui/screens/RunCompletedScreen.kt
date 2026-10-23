package com.example.simv.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simv.R
import com.example.simv.ui.theme.SimvCyan
import com.example.simv.ui.theme.SimvDarkBg
import com.example.simv.ui.theme.SimvError
import com.example.simv.ui.theme.SimvTextPrimary
import com.example.simv.ui.theme.SimvTextSecondary
import com.example.simv.ui.viewmodel.UiState

@Composable
fun RunCompletedScreen(
    totalPhotos: Int,
    formattedTime: String,
    saveState: UiState<Unit>,
    onDoneClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SimvDarkBg)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Badge de conclusão
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(SimvCyan),
                contentAlignment = Alignment.Center
            ) {
                SimvCheckIcon(
                    color = SimvDarkBg,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.run_completed_title),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = SimvTextPrimary,
                textAlign = TextAlign.Center,
                lineHeight = 28.sp,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.total_photos_format, totalPhotos),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = SimvTextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.time_format, formattedTime),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = SimvTextSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Feedback visual do estado de salvamento
            SaveStateIndicator(saveState = saveState)
        }

        Button(
            onClick = onDoneClicked,
            enabled = saveState !is UiState.Loading,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SimvCyan,
                contentColor = SimvDarkBg
            )
        ) {
            Text(
                text = stringResource(R.string.done),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}

/**
 * Componente que exibe o estado de persistência da sessão:
 * - Loading: spinner + mensagem
 * - Success: confirmação verde
 * - Error: mensagem de erro vermelha
 */
@Composable
private fun SaveStateIndicator(saveState: UiState<Unit>) {
    when (saveState) {
        is UiState.Loading -> {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = SimvCyan,
                    strokeWidth = 2.dp
                )
                Text(
                    text = "Salvando sessão...",
                    fontSize = 13.sp,
                    color = SimvTextSecondary
                )
            }
        }
        is UiState.Success -> {
            Text(
                text = "✓ Sessão salva no histórico",
                fontSize = 13.sp,
                color = SimvCyan,
                fontWeight = FontWeight.SemiBold
            )
        }
        is UiState.Error -> {
            Text(
                text = "⚠ ${saveState.message}",
                fontSize = 13.sp,
                color = SimvError,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SimvCheckIcon(
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val strokeWidth = 5.dp.toPx()
        val path = Path().apply {
            moveTo(size.width * 0.22f, size.height * 0.52f)
            lineTo(size.width * 0.42f, size.height * 0.72f)
            lineTo(size.width * 0.78f, size.height * 0.32f)
        }
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}
