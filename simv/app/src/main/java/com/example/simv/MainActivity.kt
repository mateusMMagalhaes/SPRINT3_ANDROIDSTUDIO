package com.example.simv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.simv.ui.screens.CameraViewScreen
import com.example.simv.ui.screens.RunCompletedScreen
import com.example.simv.ui.screens.SetupRunScreen
import com.example.simv.ui.screens.StartScreen
import com.example.simv.ui.theme.SimvTheme
import com.example.simv.ui.viewmodel.RunViewModel
import com.example.simv.ui.viewmodel.SimvScreen

class MainActivity : ComponentActivity() {

    private val viewModel: RunViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SimvTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SimvMainApp(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun SimvMainApp(
    viewModel: RunViewModel,
    modifier: Modifier = Modifier
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val setupForm by viewModel.setupForm.collectAsState()
    val currentSession by viewModel.currentSession.collectAsState()
    val flashTrigger by viewModel.flashTrigger.collectAsState()

    Crossfade(
        targetState = currentScreen,
        animationSpec = tween(300),
        modifier = modifier.fillMaxSize(),
        label = "ScreenTransition"
    ) { screen ->
        when (screen) {
            SimvScreen.START -> {
                StartScreen(
                    onStartRunClicked = viewModel::onStartRunClicked
                )
            }
            SimvScreen.SETUP -> {
                SetupRunScreen(
                    formState = setupForm,
                    onProjectNameChanged = viewModel::onProjectNameChanged,
                    onLocationChanged = viewModel::onLocationChanged,
                    onOperatorIdChanged = viewModel::onOperatorIdChanged,
                    onNextClicked = viewModel::onSetupNextClicked
                )
            }
            SimvScreen.CAMERA_VIEW -> {
                CameraViewScreen(
                    photosCaptured = currentSession.photosCaptured,
                    maxPhotos = currentSession.maxPhotos,
                    formattedTime = viewModel.getFormattedTime(),
                    flashTrigger = flashTrigger,
                    onTakePhotoClicked = viewModel::onTakePhotoClicked,
                    onStopRunClicked = viewModel::onStopRunClicked
                )
            }
            SimvScreen.COMPLETED -> {
                RunCompletedScreen(
                    totalPhotos = currentSession.photosCaptured,
                    formattedTime = viewModel.getFormattedTime(),
                    onDoneClicked = viewModel::onDoneClicked
                )
            }
        }
    }
}