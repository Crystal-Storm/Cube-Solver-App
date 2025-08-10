package com.example.cubesolver

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
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
    faceColors: CubeColors,
    faceIndex: Int,
    onCellClick: (faceIndex: Int, cellIndex: Int) -> Unit,
    selectedCellGlobalCoordinates: Pair<Int, Int>?,
    cellSize: Dp
) {
    val cellSpacing = 2.dp
    val faceModifier = Modifier.padding(2.dp)

    Column(
        modifier = faceModifier.border(1.dp, Color.Transparent),
        verticalArrangement = Arrangement.spacedBy(cellSpacing)
    ) {
        (0..2).forEach { rowIndex ->
            Row(horizontalArrangement = Arrangement.spacedBy(cellSpacing)) {
                (0..2).forEach { colIndex ->
                    val cellIndex = rowIndex * 3 + colIndex

                    val isThisCellSelected = selectedCellGlobalCoordinates == Pair(faceIndex, cellIndex)

                    val targetColorForThisCell = faceColors.colorValues[faceColors.colorIndices[faceIndex][cellIndex]]
                    val animatedColor by animateColorAsState(
                        targetValue = targetColorForThisCell,
                        label = "cellColorAnimation"
                    )
                    val animatedSize by animateDpAsState(
                        targetValue = cellSize,
                        label = "cellSizeAnimation"
                    )
                    Box(
                        modifier = Modifier
                            .size(animatedSize)
                            .background(animatedColor)
                            .clickable { onCellClick(faceIndex, cellIndex) }
                            .border(1.dp, Color.Transparent)
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

    val selectedCellInfo = remember { mutableStateOf<Pair<Int, Int>?>(null) }

    val normalCellSize = 35.dp

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
                            for (faceInPairIndex in 0..1){
                                FaceGridDisplay(
                                    faceColors = faceData,
                                    faceIndex = rowIndex * 2 + faceInPairIndex,
                                    onCellClick = { faceIdx, cellIdx -> cellClicked(faceIdx, cellIdx, selectedCellInfo, faceData.colorIndices) },
                                    selectedCellGlobalCoordinates = selectedCellInfo.value,
                                    cellSize = normalCellSize
                                )
                            }
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

private fun cellClicked(
    faceIndex: Int,
    cellIndex: Int,
    selectedCellState: MutableState<Pair<Int, Int>?>,
    colorIndices: List<MutableList<Int>>
) {
    val clickedInfo = Pair(faceIndex, cellIndex)

    when (selectedCellState.value) {
        null -> {
            // Selected a cell
            selectedCellState.value = clickedInfo
            Log.d("ReviewScreen", "Selected: Face $faceIndex, Cell $cellIndex")
        }
        clickedInfo -> {
            // Deselected same cell
            selectedCellState.value = null
            Log.d("ReviewScreen", "Deselected: Face $faceIndex, Cell $cellIndex")
        }
        else -> {
            // Swapped cells
            val (otherFaceIdx, otherCellIdx) = selectedCellState.value!!

            val otherFace = colorIndices[otherFaceIdx]
            val selectedFace = colorIndices[faceIndex]

            // Perform swap
            val tempIdx = otherFace[otherCellIdx]
            otherFace[otherCellIdx] = selectedFace[cellIndex]
            selectedFace[cellIndex] = tempIdx

            Log.d("ReviewScreen", "Swapped: (F${otherFaceIdx},C${otherCellIdx}) with (F${faceIndex},C${cellIndex})")

            Log.d("ReviewScreen", colorIndices.toString())

            selectedCellState.value = null
        }
    }
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