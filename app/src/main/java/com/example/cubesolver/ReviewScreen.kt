package com.example.cubesolver

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.cubesolver.ui.theme.CubeSolverTheme

@Composable
private fun FaceGridDisplay(
    faceColors: List<Color>?,
    faceIndex: Int,
    onCellClick: (faceIndex: Int, cellIndex: Int) -> Unit,
    cellSize: Dp
) {
    val cellSpacing = 2.dp
    val faceModifier = Modifier.padding(2.dp)

    if (faceColors == null) {
        Box(
            modifier = faceModifier
                .size(cellSize * 3 + cellSpacing * 2 + 4.dp)
                .background(Color.Transparent)
                .border(1.dp, Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Text("N/A", style = MaterialTheme.typography.labelSmall)
        }
        return
    }

    Column(
        modifier = faceModifier.border(1.dp, Color.Transparent),
        verticalArrangement = Arrangement.spacedBy(cellSpacing)
    ) {
        (0..2).forEach { rowIndex ->
            Row(horizontalArrangement = Arrangement.spacedBy(cellSpacing)) {
                (0..2).forEach { colIndex ->
                    val cellIndex = rowIndex * 3 + colIndex
                    val cellColor = faceColors.getOrElse(cellIndex) { Color.Gray }
                    Box(
                        modifier = Modifier
                            .size(cellSize)
                            .background(cellColor)
                            .clickable { onCellClick(faceIndex, cellIndex) }
                            .border(0.5.dp, Color.Transparent)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(navController: NavController) {

    val faceData = if (GlobalInformation.scannedFaces != null) {
        createColorClusters(GlobalInformation.scannedFaces!!)
    } else {
        null
    }

    val overallCellSize = 35.dp

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Review Scanned Faces") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (faceData != null) {
                val facePairs = faceData.colorIndices.take(6).chunked(2)

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    facePairs.forEachIndexed { rowIndex, pairOfFaces ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            FaceGridDisplay(
                                faceColors = pairOfFaces[0].map{ faceData.colorValues[it] },
                                faceIndex = rowIndex * 2,
                                onCellClick = { faceIdx, cellIdx ->
                                    Log.d("ReviewScreen", "Clicked: Face $faceIdx, Cell $cellIdx")
                                },
                                cellSize = overallCellSize
                            )
                            FaceGridDisplay(
                                faceColors = pairOfFaces[1].map{ faceData.colorValues[it] },
                                faceIndex = rowIndex * 2 + 1,
                                onCellClick = { faceIdx, cellIdx -> cellClicked(faceIdx, cellIdx) },
                                cellSize = overallCellSize
                            )
                        }
                    }
                }
                Column (
                    modifier = Modifier 
                        .fillMaxWidth() 
                        .padding(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                            // TODO: Implement "Change Color" logic
                            Log.d("ReviewScreen", "Change Color button clicked")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                    ) {
                        Text("Change Color", color = Color.White)
                    }

                    Button(
                        onClick = {
                            // TODO: Implement "Done" logic (e.g., navigate, save data)
                            Log.d("ReviewScreen", "Done button clicked")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                    ) {
                        Text("Done", color = Color.White)
                    }
                }
            } else {
                Text(
                    text = "No data to display",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

private fun cellClicked(faceIndex: Int, cellIndex: Int){
    Log.d("ReviewScreen", "Clicked: Face $faceIndex, Cell $cellIndex")
}

private fun createSampleScannedFacesForPreview(): List<List<Color>> {
    val numFaces = 6
    return List(numFaces) { faceIdx ->
        val baseHue = (faceIdx * 60f) % 360f
        List(9) { cellIdx ->
            Color.hsl(baseHue + (cellIdx * 5f) % 60f, 0.8f, 0.6f)
        }
    }
}

@Preview(showBackground = true, widthDp = 300, heightDp = 500)
@Composable
fun ReviewScreen_GridPreview_WithData() {
    GlobalInformation.scannedFaces = createSampleScannedFacesForPreview()
    CubeSolverTheme {
        ReviewScreen(navController = rememberNavController())
    }
}

@Preview(showBackground = true, widthDp = 300, heightDp = 500)
@Composable
fun ReviewScreen_GridPreview_NullData() {
    GlobalInformation.scannedFaces = null
    CubeSolverTheme {
        ReviewScreen(navController = rememberNavController())
    }
}

@Preview(showBackground = true, name = "Single FaceGridDisplay")
@Composable
fun FaceGridDisplay_Preview() {
    val sampleFace = List(9) { Color.hsl((it * 40f) % 360f, 0.9f, 0.5f) }
    CubeSolverTheme {
        FaceGridDisplay(
            faceColors = sampleFace,
            faceIndex = 0,
            onCellClick = { _, _ -> },
            cellSize = 40.dp
        )
    }
}