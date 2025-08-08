package com.example.cubesolver

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProcessingScreen(navController: NavController) { // No data arguments in the constructor

    // State to hold the data once retrieved
    var aFacesData by remember { mutableStateOf<List<List<Int>>?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // LaunchedEffect to retrieve the data once when the composable enters the composition
    LaunchedEffect(key1 = Unit) {
        Log.d("ProcessingScreen", "Attempting to retrieve data from CubeScanDataHolder.")
        // 1. GET THE DATA by accessing the property of the shared holder
        val data = ScanDataHolder.scannedFaces
        if (data != null) {
            aFacesData = data.toList() // Store a copy for this screen's state
            Log.d("ProcessingScreen", "Successfully retrieved ${aFacesData?.size ?: 0} faces.")
        } else {
            Log.w("ProcessingScreen", "No data found in CubeScanDataHolder.")
        }
        isLoading = false

        // Optional but Recommended: Clear the data in the holder after it's been retrieved.
        // This prevents stale data if ProcessingScreen is revisited without going
        // through CameraScreen's capture process.
        // CubeScanDataHolder.scannedFaces = null
    }

    // Optional but Recommended: Clear data from holder when screen is disposed
    DisposableEffect(Unit) {
        onDispose {
            Log.d("ProcessingScreen", "Disposing ProcessingScreen. Clearing data from CubeScanDataHolder.")
            // This is a good safety measure for this type of data passing.
            ScanDataHolder.scannedFaces = null
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Processed Cube Data") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                    Text("Loading Scan Data...")
                }
            }
            aFacesData.isNullOrEmpty() -> {
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    Text("No scan data available or failed to load.")
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Log.d("ProcessingScreen", "Start clustering colors")
                    val roundedColors = createColorClusters(aFacesData!!)
                    Log.d("ProcessingScreen", "Finished clustering colors")

                    itemsIndexed(roundedColors.colorIndices) { faceIndex, colorListForFace ->
                        Column {
                            Text("Face ${faceIndex + 1}:", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                colorListForFace.forEach { colorIndex ->
                                    Box(
                                        modifier = Modifier
                                            .size(30.dp)
                                            .background(roundedColors.colorValues[colorIndex], CircleShape)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}