package com.example.cubesolver

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.cubesolver.ui.theme.CubeSolverTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolutionScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Solution") },
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
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Column{
                if (GlobalInformation.cubeState != null) {
                    val cubeState = GlobalInformation.cubeState!!
                    val solutionState = cubeState.endState()

                    val solver = Solver(cubeState, solutionState)

                    val solution = solver.bidirectionalSearch()

                    CubeStateView(cubeState)
                    Text("Solution: $solution")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SolvingScreenPreview() {
    val cubeState = CubeState()
    cubeState.rotateUp(1)
    cubeState.rotateLeft(2)
    cubeState.rotateFront(3)
    GlobalInformation.cubeState = cubeState
    CubeSolverTheme {
        SolutionScreen(navController = rememberNavController())
    }
}
