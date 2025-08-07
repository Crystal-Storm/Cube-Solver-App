package com.example.cubesolver

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cubesolver.ui.theme.CubeSolverTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CubeSolverTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "greeting") {
                    composable("greeting") {
                        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                            Greeting(
                                navController = navController,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                    composable("cameraScreen") {
                        // Pass the navController to CameraScreen
                        CameraScreen(navController = navController)
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(navController: NavController, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = { navController.navigate("cameraScreen") },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
        ) {
            Text("text")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CubeSolverTheme {
        // Previewing Greeting requires a NavController, so we create a dummy one.
        val navController = rememberNavController()
        Greeting(navController = navController)
    }
}