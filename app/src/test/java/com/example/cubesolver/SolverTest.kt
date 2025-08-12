package com.example.cubesolver

import org.junit.Assert.assertEquals
import org.junit.Test

class SolverTest {

    @Test
    fun breadthFirstSearch_fiveMovesScramble() {
        val scrambledState = CubeState()

        scrambledState.rotateFront(1)    // F
        scrambledState.rotateUp(1)       // U
        scrambledState.rotateRight(1)    // R
        scrambledState.rotateDown(3)     // D'
//        scrambledState.rotateBack(2)     // B2

        val targetState = CubeState() // Target is the solved state

        val solverInstance = Solver(scrambledState, targetState)
        val solutionPath = solverInstance.breadthFirstSearch()
        val expectedSolution = "D R' U' F' "

        assertEquals("Solution path for 5-move scramble is incorrect", expectedSolution, solutionPath)
    }
}