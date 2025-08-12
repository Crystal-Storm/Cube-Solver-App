package com.example.cubesolver

import androidx.compose.ui.graphics.Color

class CubeState() {
    var faceColors = listOf(
        Color.White,
        Color(0xFFFFA500),
        Color.Green,
        Color.Red,
        Color.Blue,
        Color.Yellow
    )
    var upFace: MutableList<Int> = MutableList(9) { 0 }
    var leftFace: MutableList<Int> = MutableList(9) { 1 }
    var frontFace: MutableList<Int> = MutableList(9) { 2 }
    var rightFace: MutableList<Int> = MutableList(9) { 3 }
    var backFace: MutableList<Int> = MutableList(9) { 4 }
    var downFace: MutableList<Int> = MutableList(9) { 5 }

    constructor(faces: List<List<Int>>) : this() {
        if (faces.size == 6 && faces.all { it.size == 9 }) {
            faces[0].forEachIndexed { i, color -> upFace[i] = color }
            faces[1].forEachIndexed { i, color -> leftFace[i] = color }
            faces[2].forEachIndexed { i, color -> frontFace[i] = color }
            faces[3].forEachIndexed { i, color -> rightFace[i] = color }
            faces[4].forEachIndexed { i, color -> backFace[i] = color }
            faces[5].forEachIndexed { i, color -> downFace[i] = color }
        }
    }

    constructor(cubeColors: CubeColors) : this() {
        faceColors = cubeColors.colorValues

        val faces = cubeColors.colorIndices

        upFace = faces[0].toMutableList()
        frontFace = faces[1].toMutableList()
        rightFace = faces[2].toMutableList()
        downFace = faces[3].toMutableList()
        backFace = faces[4].toMutableList()
        leftFace = faces[5].toMutableList()

        rotateFaceStickersClockwise(downFace)
        rotateFaceStickersCounterClockwise(backFace)
        rotateFaceStickersCounterClockwise(leftFace)
    }

    private fun rotateFaceStickersClockwise(face: MutableList<Int>) {
        val tempCorner = face[0]
        face[0] = face[6]
        face[6] = face[8]
        face[8] = face[2]
        face[2] = tempCorner

        val tempEdge = face[1]
        face[1] = face[3]
        face[3] = face[7]
        face[7] = face[5]
        face[5] = tempEdge
    }

    private fun rotateFaceStickersCounterClockwise(face: MutableList<Int>) {
        val tempCorner = face[0]
        face[0] = face[2]
        face[2] = face[8]
        face[8] = face[6]
        face[6] = tempCorner

        val tempEdge = face[1]
        face[1] = face[5]
        face[5] = face[7]
        face[7] = face[3]
        face[3] = tempEdge
    }

    private fun performFrontClockwise() {
        rotateFaceStickersClockwise(frontFace)

        val tu0 = upFace[6]; val tu1 = upFace[7]; val tu2 = upFace[8]
        upFace[6] = leftFace[8]; upFace[7] = leftFace[5]; upFace[8] = leftFace[2]
        leftFace[8] = downFace[2]; leftFace[5] = downFace[1]; leftFace[2] = downFace[0]
        downFace[2] = rightFace[0]; downFace[1] = rightFace[3]; downFace[0] = rightFace[6]
        rightFace[0] = tu0; rightFace[3] = tu1; rightFace[6] = tu2
    }

    private fun performBackClockwise() {
        rotateFaceStickersClockwise(backFace)

        val tu0 = upFace[2]; val tu1 = upFace[1]; val tu2 = upFace[0]
        upFace[2] = rightFace[8]; upFace[1] = rightFace[5]; upFace[0] = rightFace[2]
        rightFace[8] = downFace[6]; rightFace[5] = downFace[7]; rightFace[2] = downFace[8]
        downFace[6] = leftFace[0]; downFace[7] = leftFace[3]; downFace[8] = leftFace[6]
        leftFace[0] = tu0; leftFace[3] = tu1; leftFace[6] = tu2
    }

    private fun performUpClockwise() {
        rotateFaceStickersClockwise(upFace)

        val tf0 = frontFace[0]; val tf1 = frontFace[1]; val tf2 = frontFace[2]
        frontFace[0] = rightFace[0]; frontFace[1] = rightFace[1]; frontFace[2] = rightFace[2]
        rightFace[0] = backFace[0]; rightFace[1] = backFace[1]; rightFace[2] = backFace[2]
        backFace[0] = leftFace[0]; backFace[1] = leftFace[1]; backFace[2] = leftFace[2]
        leftFace[0] = tf0; leftFace[1] = tf1; leftFace[2] = tf2
    }

    private fun performDownClockwise() {
        rotateFaceStickersClockwise(downFace)

        val tf0 = frontFace[6]; val tf1 = frontFace[7]; val tf2 = frontFace[8]
        frontFace[6] = leftFace[6]; frontFace[7] = leftFace[7]; frontFace[8] = leftFace[8]
        leftFace[6] = backFace[6]; leftFace[7] = backFace[7]; leftFace[8] = backFace[8]
        backFace[6] = rightFace[6]; backFace[7] = rightFace[7]; backFace[8] = rightFace[8]
        rightFace[6] = tf0; rightFace[7] = tf1; rightFace[8] = tf2
    }

    private fun performLeftClockwise() {
        rotateFaceStickersClockwise(leftFace)

        val tu0 = upFace[0]; val tu1 = upFace[3]; val tu2 = upFace[6]
        upFace[0] = backFace[8]; upFace[3] = backFace[5]; upFace[6] = backFace[2]
        backFace[8] = downFace[0]; backFace[5] = downFace[3]; backFace[2] = downFace[6]
        downFace[0] = frontFace[0]; downFace[3] = frontFace[3]; downFace[6] = frontFace[6]
        frontFace[0] = tu0; frontFace[3] = tu1; frontFace[6] = tu2
    }

    private fun performRightClockwise() {
        rotateFaceStickersClockwise(rightFace)

        val tu0 = upFace[2]; val tu1 = upFace[5]; val tu2 = upFace[8]
        upFace[2] = frontFace[2]; upFace[5] = frontFace[5]; upFace[8] = frontFace[8]
        frontFace[2] = downFace[2]; frontFace[5] = downFace[5]; frontFace[8] = downFace[8]
        downFace[2] = backFace[6]; downFace[5] = backFace[3]; downFace[8] = backFace[0]
        backFace[6] = tu0; backFace[3] = tu1; backFace[0] = tu2
    }

    private fun performFrontCounterClockwise() {
        rotateFaceStickersCounterClockwise(frontFace)

        val tu0 = upFace[6]; val tu1 = upFace[7]; val tu2 = upFace[8]
        upFace[6] = rightFace[0]; upFace[7] = rightFace[3]; upFace[8] = rightFace[6]
        rightFace[0] = downFace[2]; rightFace[3] = downFace[1]; rightFace[6] = downFace[0]
        downFace[2] = leftFace[8]; downFace[1] = leftFace[5]; downFace[0] = leftFace[2]
        leftFace[8] = tu0; leftFace[5] = tu1; leftFace[2] = tu2
    }

    private fun performBackCounterClockwise() {
        rotateFaceStickersCounterClockwise(backFace)

        val tu0 = upFace[2]; val tu1 = upFace[1]; val tu2 = upFace[0]
        upFace[2] = leftFace[0]; upFace[1] = leftFace[3]; upFace[0] = leftFace[6]
        leftFace[0] = downFace[6]; leftFace[3] = downFace[7]; leftFace[6] = downFace[8]
        downFace[6] = rightFace[8]; downFace[7] = rightFace[5]; downFace[8] = rightFace[2]
        rightFace[8] = tu0; rightFace[5] = tu1; rightFace[2] = tu2
    }

    private fun performUpCounterClockwise() {
        rotateFaceStickersCounterClockwise(upFace)

        val tf0 = frontFace[0]; val tf1 = frontFace[1]; val tf2 = frontFace[2]
        frontFace[0] = leftFace[0]; frontFace[1] = leftFace[1]; frontFace[2] = leftFace[2]
        leftFace[0] = backFace[0]; leftFace[1] = backFace[1]; leftFace[2] = backFace[2]
        backFace[0] = rightFace[0]; backFace[1] = rightFace[1]; backFace[2] = rightFace[2]
        rightFace[0] = tf0; rightFace[1] = tf1; rightFace[2] = tf2
    }

    private fun performDownCounterClockwise() {
        rotateFaceStickersCounterClockwise(downFace)

        val tf0 = frontFace[6]; val tf1 = frontFace[7]; val tf2 = frontFace[8]
        frontFace[6] = rightFace[6]; frontFace[7] = rightFace[7]; frontFace[8] = rightFace[8]
        rightFace[6] = backFace[6]; rightFace[7] = backFace[7]; rightFace[8] = backFace[8]
        backFace[6] = leftFace[6]; backFace[7] = leftFace[7]; backFace[8] = leftFace[8]
        leftFace[6] = tf0; leftFace[7] = tf1; leftFace[8] = tf2
    }

    private fun performLeftCounterClockwise() {
        rotateFaceStickersCounterClockwise(leftFace)

        val tu0 = upFace[0]; val tu1 = upFace[3]; val tu2 = upFace[6]
        upFace[0] = frontFace[0]; upFace[3] = frontFace[3]; upFace[6] = frontFace[6]
        frontFace[0] = downFace[0]; frontFace[3] = downFace[3]; frontFace[6] = downFace[6]
        downFace[0] = backFace[8]; downFace[3] = backFace[5]; downFace[6] = backFace[2]
        backFace[8] = tu0; backFace[5] = tu1; backFace[2] = tu2
    }

    private fun performRightCounterClockwise() {
        rotateFaceStickersCounterClockwise(rightFace)

        val tu0 = upFace[2]; val tu1 = upFace[5]; val tu2 = upFace[8]
        upFace[2] = backFace[6]; upFace[5] = backFace[3]; upFace[8] = backFace[0]
        backFace[6] = downFace[2]; backFace[3] = downFace[5]; backFace[0] = downFace[8]
        downFace[2] = frontFace[2]; downFace[5] = frontFace[5]; downFace[8] = frontFace[8]
        frontFace[2] = tu0; frontFace[5] = tu1; frontFace[8] = tu2
    }

    private fun normalizeRotations(rotations: Int): Int = (rotations % 4 + 4) % 4

    fun rotateFront(numOfRotations: Int) {
        when (normalizeRotations(numOfRotations)) {
            1 -> performFrontClockwise()
            2 -> { performFrontClockwise(); performFrontClockwise() }
            3 -> performFrontCounterClockwise()
        }
    }

    fun rotateBack(numOfRotations: Int) {
        when (normalizeRotations(numOfRotations)) {
            1 -> performBackClockwise()
            2 -> { performBackClockwise(); performBackClockwise() }
            3 -> performBackCounterClockwise()
        }
    }

    fun rotateUp(numOfRotations: Int) {
        when (normalizeRotations(numOfRotations)) {
            1 -> performUpClockwise()
            2 -> { performUpClockwise(); performUpClockwise() }
            3 -> performUpCounterClockwise()
        }
    }

    fun rotateDown(numOfRotations: Int) {
        when (normalizeRotations(numOfRotations)) {
            1 -> performDownClockwise()
            2 -> { performDownClockwise(); performDownClockwise() }
            3 -> performDownCounterClockwise()
        }
    }

    fun rotateLeft(numOfRotations: Int) {
        when (normalizeRotations(numOfRotations)) {
            1 -> performLeftClockwise()
            2 -> { performLeftClockwise(); performLeftClockwise() }
            3 -> performLeftCounterClockwise()
        }
    }

    fun rotateRight(numOfRotations: Int) {
        when (normalizeRotations(numOfRotations)) {
            1 -> performRightClockwise()
            2 -> { performRightClockwise(); performRightClockwise() }
            3 -> performRightCounterClockwise()
        }
    }

    fun copy(): CubeState {
        val newFaces = listOf(
            ArrayList(upFace),
            ArrayList(leftFace),
            ArrayList(frontFace),
            ArrayList(rightFace),
            ArrayList(backFace),
            ArrayList(downFace)
        )
        return CubeState(newFaces)
    }

    override fun toString(): String {
        val nl = System.lineSeparator()
        fun faceToString(face: List<Int>, name: String): String {
            return "$name:$nl" +
                   "  ${face[0]} ${face[1]} ${face[2]}$nl" +
                   "  ${face[3]} ${face[4]} ${face[5]}$nl" +
                   "  ${face[6]} ${face[7]} ${face[8]}$nl"
        }
        return faceToString(upFace, "Up") +
               faceToString(leftFace, "Left") +
               faceToString(frontFace, "Front") +
               faceToString(rightFace, "Right") +
               faceToString(backFace, "Back") +
               faceToString(downFace, "Down")
    }
}
