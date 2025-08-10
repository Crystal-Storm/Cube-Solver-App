package com.example.cubesolver

import androidx.compose.ui.graphics.Color
import kotlin.math.sqrt

// Helper function to calculate the average of a list of colors
private fun calculateMeanColor(colorsInCluster: List<Color>): Color {
    if (colorsInCluster.isEmpty()) {
        return Color(128, 128, 128)
    }
    var sumR = 0f
    var sumG = 0f
    var sumB = 0f
    colorsInCluster.forEach { color ->
        sumR += color.red
        sumG += color.green
        sumB += color.blue
    }
    val count = colorsInCluster.size.toFloat()
    return Color(red = sumR / count, green = sumG / count, blue = sumB / count)
}

fun createColorClusters(inputColorLists: List<List<Color>>, maxIterations: Int = 100): CubeColors {

    // flatten list for easier iteration
    val allColorsFlat: List<Color> = inputColorLists.flatten()

    // Get middle color to use as a start for the centroids
//    var currentCentroids: MutableList<Color> = facesAsColors.map { faceColors ->
//        faceColors[4]
//    }.toMutableList()
    var currentCentroids: MutableList<Color> = mutableListOf(
        Color.White,
        Color.Green,
        Color.Red,
        Color.Yellow,
        Color.Blue,
        Color(0xFF, 0x5C, 0x00)
    )

    val k = currentCentroids.size

    var iterations = 0
    var changed = true

    // Create a list of clusters
    var clusters: MutableList<MutableList<Color>> = MutableList(k) { mutableListOf() }

    while (changed && iterations < maxIterations) {
        val newClusters: MutableList<MutableList<Color>> = MutableList(k) { mutableListOf() } // Initialize empty clusters for this iteration

        // For every color, put it in the closest centroid cluster
        for (color in allColorsFlat) {
            var nearestCentroidIndex = -1
            var minDistance = Double.MAX_VALUE
            currentCentroids.forEachIndexed { index, centroid ->
                val distance = color.distanceTo(centroid)
                if (distance < minDistance) {
                    minDistance = distance
                    nearestCentroidIndex = index
                }
            }
            newClusters[nearestCentroidIndex].add(color)
        }

        val newCentroids = mutableListOf<Color>()

        // Set the centroids to be at the mean of each cluster
        for (i in 0 until k) {
            val currentCluster = newClusters[i]

            val newCentroid = if (!currentCluster.isEmpty()) {
                calculateMeanColor(currentCluster)
            } else {
                Color.Black
            }
            newCentroids.add(newCentroid)
        }

        // For any clusters that are empty, remove the farthest color from an overflowing cluster
        for ((index, _) in newClusters.withIndex().filter { it.value.isEmpty() }) {
            val movedColor = popOverflowOutlier(newClusters, newCentroids)
            newCentroids[index] = movedColor
        }

        // Check to see if the clusters have not changed
        if (newClusters == clusters) {
            changed = false
        }

        clusters = newClusters.toMutableList()

        currentCentroids = newCentroids.toMutableList()
        iterations++
    }

    // Even out the sizes of the clusters to each contain 9
    while (clusters.any { it.size > 9 }) {
        val movingColor = popOverflowOutlier(clusters, currentCentroids)

        var nearest = Double.MAX_VALUE
        var indexNearest = -1
        for (underflow in clusters.filter { it.size < 9 }) {
            val centroidIndex = clusters.indexOf(underflow)
            val distance = movingColor.distanceTo(currentCentroids[centroidIndex])
            if (distance < nearest) {
                nearest = distance
                indexNearest = centroidIndex
            }
        }
        clusters[indexNearest].add(movingColor)
    }

    // Reorganize the data to be in a format that is easily readable and understandable
    val colorIndices: MutableList<MutableList<Int>> = MutableList(6) { mutableListOf() }
    val colorValues = currentCentroids

    for ((face, list) in inputColorLists.withIndex()){
        for (color in list){
            // See if a cluster contains the wanted color
            for ((position, cluster) in clusters.withIndex()){
                if (cluster.contains(color)){
                    // if so remove the color and add the corresponding index to be returned
                    cluster.remove(color)
                    colorIndices[face].add(position)
                    break
                }
            }
        }
    }

    return CubeColors(colorValues, colorIndices)
}

private fun popOverflowOutlier(clusters: MutableList<MutableList<Color>>, centroids: List<Color>): Color {

    // Get an overflowing cluster
    val overflow = clusters.first { it.size > 9 }
    val centroidIndex = clusters.indexOf(overflow)
    var farthest = 0.0
    var index = -1
    // Find the color that is farthest from the current centroid of the cluster it is in
    for (color in overflow) {
        val distance = color.distanceTo(centroids[centroidIndex])
        if (index == -1 || farthest < distance) {
            index = overflow.indexOf(color)
            farthest = distance
        }
    }
    // Move the color to the closest centroid cluster that has less than 9 colors
    val movingColor = overflow.removeAt(index)
    return movingColor
}

private fun Color.distanceTo(other: Color): Double {
    val r1 = this.red
    val g1 = this.green
    val b1 = this.blue
    val r2 = other.red
    val g2 = other.green
    val b2 = other.blue

    // Euclidean distance in RGB space
    val dr = (r1 - r2).toDouble()
    val dg = (g1 - g2).toDouble()
    val db = (b1 - b2).toDouble()

    return sqrt(dr * dr + dg * dg + db * db)
}

data class CubeColors(val colorValues: MutableList<Color>, val colorIndices: List<MutableList<Int>>)