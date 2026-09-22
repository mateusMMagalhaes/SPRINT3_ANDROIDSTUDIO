package com.example.simv.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.simv.R
import com.example.simv.ui.theme.SimvCyan
import com.example.simv.ui.theme.SimvDarkBg
import com.example.simv.ui.theme.SimvDarkButtonBg
import com.example.simv.ui.theme.SimvTextPrimary
import com.example.simv.ui.theme.SimvTextSecondary

@Composable
fun CameraViewScreen(
    photosCaptured: Int,
    maxPhotos: Int,
    formattedTime: String,
    flashTrigger: Boolean,
    onTakePhotoClicked: () -> Unit,
    onStopRunClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }

    val capturePhotoAction = {
        onTakePhotoClicked()
        imageCapture?.let { capture ->
            try {
                capture.takePicture(
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageCapturedCallback() {
                        override fun onCaptureSuccess(image: ImageProxy) {
                            image.close()
                        }
                        override fun onError(exception: androidx.camera.core.ImageCaptureException) {
                            exception.printStackTrace()
                        }
                    }
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SimvDarkBg)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Camera View Box (Top ~45% of screen)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xFF1E252D))
            ) {
                if (hasCameraPermission) {
                    AndroidView(
                        factory = { ctx ->
                            val previewView = PreviewView(ctx)
                            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                            cameraProviderFuture.addListener({
                                val cameraProvider = cameraProviderFuture.get()
                                val preview = Preview.Builder().build().also {
                                    it.setSurfaceProvider(previewView.surfaceProvider)
                                }
                                val capture = ImageCapture.Builder().build()
                                imageCapture = capture

                                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                                try {
                                    cameraProvider.unbindAll()
                                    cameraProvider.bindToLifecycle(
                                        lifecycleOwner,
                                        cameraSelector,
                                        preview,
                                        capture
                                    )
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }, ContextCompat.getMainExecutor(ctx))

                            previewView
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Permissão de Câmera Necessária",
                            color = SimvTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                            colors = ButtonDefaults.buttonColors(containerColor = SimvCyan)
                        ) {
                            Text("Conceder Permissão", color = SimvDarkBg, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Rule of thirds grid lines overlay
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 1.dp.toPx()
                    val gridColor = Color.White.copy(alpha = 0.25f)

                    // Vertical lines
                    val oneThirdW = size.width / 3f
                    val twoThirdsW = oneThirdW * 2f
                    drawLine(gridColor, Offset(oneThirdW, 0f), Offset(oneThirdW, size.height), strokeWidth)
                    drawLine(gridColor, Offset(twoThirdsW, 0f), Offset(twoThirdsW, size.height), strokeWidth)

                    // Horizontal lines
                    val oneThirdH = size.height / 3f
                    val twoThirdsH = oneThirdH * 2f
                    drawLine(gridColor, Offset(0f, oneThirdH), Offset(size.width, oneThirdH), strokeWidth)
                    drawLine(gridColor, Offset(0f, twoThirdsH), Offset(size.width, twoThirdsH), strokeWidth)
                }

                // CAMERA VIEW Title Overlay
                Text(
                    text = stringResource(R.string.camera_view),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = SimvTextPrimary,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.align(Alignment.TopCenter).padding(top = 16.dp)
                )

                // Live Camera Flash Animation Overlay
                if (flashTrigger) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White.copy(alpha = 0.75f))
                    )
                }
            }

            // Controls & Metrics Section (Bottom ~55% of screen)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Big Circular "TAKE PHOTO" Button
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .background(SimvCyan)
                        .clickable { capturePhotoAction() },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        SimvCameraIcon(
                            color = SimvDarkBg,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.take_photo),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = SimvDarkBg
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Photos Taken Counter
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "PHOTOS TAKEN: ",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SimvTextPrimary,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "$photosCaptured",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = SimvCyan
                    )
                    Text(
                        text = " / $maxPhotos",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SimvTextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Timer Indicator
                Text(
                    text = "Time: $formattedTime",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = SimvTextSecondary
                )

                Spacer(modifier = Modifier.height(24.dp))

                // STOP RUN Button
                Button(
                    onClick = onStopRunClicked,
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(48.dp)
                        .border(1.dp, SimvTextSecondary.copy(alpha = 0.3f), RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SimvDarkButtonBg,
                        contentColor = SimvTextPrimary
                    )
                ) {
                    Text(
                        text = stringResource(R.string.stop_run),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun SimvCameraIcon(
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val strokeWidth = 2.5.dp.toPx()
        val w = size.width
        val h = size.height

        drawRoundRect(
            color = color,
            topLeft = Offset(w * 0.1f, h * 0.32f),
            size = Size(w * 0.8f, h * 0.52f),
            cornerRadius = CornerRadius(4.dp.toPx()),
            style = Stroke(width = strokeWidth)
        )
        drawRoundRect(
            color = color,
            topLeft = Offset(w * 0.35f, h * 0.18f),
            size = Size(w * 0.3f, h * 0.14f),
            cornerRadius = CornerRadius(2.dp.toPx()),
            style = Stroke(width = strokeWidth)
        )
        drawCircle(
            color = color,
            radius = w * 0.18f,
            center = Offset(w * 0.5f, h * 0.58f),
            style = Stroke(width = strokeWidth)
        )
    }
}
