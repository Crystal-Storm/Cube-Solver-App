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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.cubesolver.ui.theme.CubeSolverTheme

val colorPickerOptions = listOf(
    Color.Red,
    Color(0xFF, 0x5C, 0x00),
    Color.Yellow,
    Color.Green,
    Color.Blue,
    Color.White
)

@Composable
private fun FaceGridDisplay(
    faceColors: CubeColors,
    faceIndex: Int,
    onCellClick: (faceIndex: Int, cellIndex: Int) -> Unit,
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
    val context = LocalContext.current

    val faceData = remember { mutableStateOf(
        if (GlobalInformation.scannedFaces != null && GlobalInformation.cubeIndices == null) {
            createColorClusters(GlobalInformation.scannedFaces!!)
        } else if (GlobalInformation.cubeIndices != null) {
            GlobalInformation.cubeIndices
        } else {
            null
        }
    )}

    val selectedCellInfo = remember { mutableStateOf<Pair<Int, Int>?>(null) }

    val showColorOptions = remember { mutableStateOf(false) }

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
            if (faceData.value != null) {
                val facePairs = faceData.value!!.colorIndices.take(6).chunked(2)

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
                                    faceColors = faceData.value!!,
                                    faceIndex = rowIndex * 2 + faceInPairIndex,
                                    onCellClick = { faceIdx, cellIdx -> cellClicked(
                                        faceIdx,
                                        cellIdx,
                                        selectedCellInfo,
                                        faceData
                                    ) },
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
                            showColorOptions.value = !showColorOptions.value
                        },
                        enabled = selectedCellInfo.value != null,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                    ) {
                        Text("Change Color", color = Color.White)
                    }

                    Button(
                        onClick = {
                            val mainExecutor = ContextCompat.getMainExecutor(context)
                            mainExecutor.execute {
                                GlobalInformation.cubeIndices = faceData.value

                                navController.navigate("solutionScreen")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                    ) {
                        Text("Done", color = Color.White)
                    }
                    if (showColorOptions.value) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            colorPickerOptions.forEach { colorOption ->
                                Box(modifier = Modifier
                                    .size(40.dp)
                                    .background(colorOption.copy(alpha = if (selectedCellInfo.value == null) 0.5f else 1f))
                                    .clickable {
                                        if (selectedCellInfo.value != null) {
                                            val pair = selectedCellInfo.value!!
                                            val colorIndex = faceData.value!!.colorIndices[pair.first][pair.second]

                                            faceData.value!!.colorValues[colorIndex] = colorOption

                                            selectedCellInfo.value = null

                                            showColorOptions.value = false
                                        }
                                    }
                                )
                            }
                        }
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
    faceData: MutableState<CubeColors?>
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

            val otherFace = faceData.value!!.colorIndices[otherFaceIdx]
            val selectedFace = faceData.value!!.colorIndices[faceIndex]

            // Perform swap
            val tempIdx = otherFace[otherCellIdx]
            otherFace[otherCellIdx] = selectedFace[cellIndex]
            selectedFace[cellIndex] = tempIdx

            Log.d("ReviewScreen", "Swapped: (F${otherFaceIdx},C${otherCellIdx}) with (F${faceIndex},C${cellIndex})")

            Log.d("ReviewScreen", faceData.value!!.colorIndices.toString())

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