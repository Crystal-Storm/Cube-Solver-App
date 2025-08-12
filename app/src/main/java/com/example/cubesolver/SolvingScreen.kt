package com.example.cubesolver

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CubeFaceView(
    faceIndices: List<Int>,
    faceColors: List<Color>,
    stickerSize: Dp = 30.dp,
    spacing: Dp = 2.dp
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(spacing),
        modifier = Modifier
    ) {
        for (i in 0..2) {
            Row(horizontalArrangement = Arrangement.spacedBy(spacing)) {
                for (j in 0..2) {
                    val colorIndex = faceIndices[i * 3 + j]
                    Box(
                        modifier = Modifier
                            .size(stickerSize)
                            .background(faceColors[colorIndex])
                    )
                }
            }
        }
    }
}

@Composable
fun CubeStateView(
    cubeState: CubeState,
    stickerSize: Dp = 20.dp, 
    faceSpacing: Dp = 4.dp
) {
    val faceColors = cubeState.faceColors
    val cubeFaceViewInternalSpacing = 2.dp 
    val cubeFaceViewBorder = 1.dp
    val actualCubeFaceViewWidth = (stickerSize * 3) + (cubeFaceViewInternalSpacing * 2) + (cubeFaceViewBorder * 2)

    val spacerWidthForAlignment = (actualCubeFaceViewWidth + faceSpacing - 6.dp)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(faceSpacing),
        modifier = Modifier.padding(16.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(faceSpacing)) {
            CubeFaceView(cubeState.upFace, faceColors, stickerSize)
            Box(modifier = Modifier.width(spacerWidthForAlignment)) // Spacer to align Up with Front
        }
        Row(horizontalArrangement = Arrangement.spacedBy(faceSpacing)) {
            CubeFaceView(cubeState.leftFace, faceColors, stickerSize)  // Left
            CubeFaceView(cubeState.frontFace, faceColors, stickerSize) // Front
            CubeFaceView(cubeState.rightFace, faceColors, stickerSize) // Right
            CubeFaceView(cubeState.backFace, faceColors, stickerSize)  // Back
        }
        Row(horizontalArrangement = Arrangement.spacedBy(faceSpacing)) {
            CubeFaceView(cubeState.downFace, faceColors, stickerSize)
            Box(modifier = Modifier.width(spacerWidthForAlignment)) // Spacer to align Down with Front
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SolvedCubePreview() {
    val solvedCube = CubeState()
    solvedCube.rotateUp(1)
    solvedCube.rotateLeft(1)
    solvedCube.rotateFront(3)
    solvedCube.rotateDown(2)
    solvedCube.rotateRight(3)
    solvedCube.rotateBack(1)
    MaterialTheme {
        CubeStateView(cubeState = solvedCube, stickerSize = 25.dp)
    }
}