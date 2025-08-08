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
import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.core.graphics.get
import androidx.compose.material3.*
import androidx.compose.runtime.*

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

    var faceCount by remember { mutableIntStateOf(0) }
    val extractedFaces = remember { mutableListOf<List<Int>>() }

    LaunchedEffect(key1 = true) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (faceCount < 6) {
                        Text("Scan face ${faceCount + 1} of 6")
                    } else {
                        Text("Processing...")
                    }
                },
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
                                preview
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
                        .clickable {
                            if (faceCount < 6) {
                                val bitmap = previewView.bitmap

                                if (bitmap != null){

                                    Log.d("CameraScreen", "Image captured")

                                    val extractedColors = getColors(bitmap)

                                    extractedFaces.add(extractedColors.toList())
                                    faceCount++

                                    Log.d("CameraScreen", "Extracted ${extractedColors.size} colors: $extractedColors")

                                    if (faceCount == 6) {
                                        val mainExecutor = ContextCompat.getMainExecutor(context)
                                        mainExecutor.execute {
                                            ScanDataHolder.scannedFaces = extractedFaces.toList()

                                            navController.navigate("processingScreen")
                                        }
                                    }
                                }
                            }
                        }
                )

            } else {
                Text("Camera permission is required to use this feature.")
            }
        }
    }
}

fun getColors(bitmap: Bitmap): List<Int> {
    val bmpWidth = bitmap.width
    val bmpHeight = bitmap.height

    val squareSize = bmpWidth.coerceAtMost(bmpHeight) * 0.8f
    val topLeftX = (bmpWidth - squareSize) / 2
    val topLeftY = (bmpHeight - squareSize) / 2
    val thirdOfSquareSize = squareSize / 3
    val centerOffset = thirdOfSquareSize / 2

    val extractedColors = mutableListOf<Int>()

    for (rowIndex in 0..2){
        for (columnIndex in 0..2){
            val x = topLeftX + columnIndex * thirdOfSquareSize + centerOffset
            val y = topLeftY + rowIndex * thirdOfSquareSize + centerOffset

            val pixelColor = bitmap[x.toInt(), y.toInt()]
            extractedColors.add(pixelColor)
        }
    }
    return extractedColors
}

@ComposePreview(showBackground = true)
@Composable
fun CameraScreenPreview() {
    CubeSolverTheme {
        CameraScreen(navController = rememberNavController())
    }
}