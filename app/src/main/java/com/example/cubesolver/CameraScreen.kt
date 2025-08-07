package com.example.cubesolver

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview as ComposePreview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.cubesolver.ui.theme.CubeSolverTheme
import androidx.camera.core.ImageCapture // For ImageCapture use case
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import androidx.compose.runtime.DisposableEffect
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(navController: NavController, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
        }
    )

    // Remember an ImageCapture use case
    val imageCapture = remember { ImageCapture.Builder().build() }

    // Remember a single-threaded executor for ImageCapture
    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }

    // Clean up the executor when the composable is disposed
    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }

    LaunchedEffect(key1 = true) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Show the faces of the Cube") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            if (hasCameraPermission) {
                // val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }
                val previewView = remember { PreviewView(context) }

                LaunchedEffect(Unit) {
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }
                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                        try {
                            cameraProvider.unbindAll() // Unbind use cases before rebinding
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                imageCapture
                            )
                        } catch (exc: Exception) {
                            Log.e("CameraScreen", "Use case binding failed", exc)
                        }
                    }, ContextCompat.getMainExecutor(context))
                }

                AndroidView({ previewView }, modifier = Modifier.fillMaxSize())

                // Overlay
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height
                    val squareSize = canvasWidth.coerceAtMost(canvasHeight) * 0.8f // 80% of shorter dimension
                    val squareTopLeft = Offset(
                        (canvasWidth - squareSize) / 2,
                        (canvasHeight - squareSize) / 2
                    )
                    val thirdOfSquareSize = squareSize / 3

                    // Draw semi-transparent gray background
                    drawRect(
                        color = Color.LightGray.copy(alpha = 0.5f),
                        size = Size(canvasWidth, canvasHeight)
                    )

                    // Clear the central square
                    drawRect(
                        color = Color.Transparent, // Using Color.Transparent to show what'''s underneath
                        topLeft = squareTopLeft,
                        size = Size(squareSize, squareSize),
                        blendMode = BlendMode.Clear // Clears the pixels in this rectangle
                    )

                    // Draw white border for the square
                    drawRect(
                        color = Color.White,
                        topLeft = squareTopLeft,
                        size = Size(squareSize, squareSize),
                        style = Stroke(width = 4.dp.toPx())
                    )

                    // Draw the grid pattern
                    drawRect(
                        color = Color.White,
                        topLeft = squareTopLeft + Offset(thirdOfSquareSize, 0f),
                        size = Size(thirdOfSquareSize, squareSize),
                        style = Stroke(width = 2.dp.toPx())
                    )
                    drawRect(
                        color = Color.White,
                        topLeft = squareTopLeft + Offset(0f, thirdOfSquareSize),
                        size = Size(squareSize, thirdOfSquareSize),
                        style = Stroke(width = 2.dp.toPx())
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 32.dp)
                        .size(72.dp) // Size of the circular button
                        .clip(CircleShape)
                        .background(Color.LightGray)
                        .border(BorderStroke(5.dp, Color.White), CircleShape)
                        .clickable { imageCapture.takePicture(
                            cameraExecutor,
                            object : ImageCapture.OnImageCapturedCallback() {
                                override fun onCaptureSuccess(image: ImageProxy) {
                                    Log.d("CameraScreen", "Image captured")
                                    // Process image here
                                    image.close()
                                }

                                override fun onError(exception: ImageCaptureException) {
                                    Log.e("CameraScreen", "Image capture failed: ${exception.message}", exception)
                                    // Handle error
                                }
                            }
                        ) }
                )

            } else {
                Text("Camera permission is required to use this feature.")
            }
        }
    }
}

@ComposePreview(showBackground = true)
@Composable
fun CameraScreenPreview() {
    CubeSolverTheme {
        CameraScreen(navController = rememberNavController())
    }
}
