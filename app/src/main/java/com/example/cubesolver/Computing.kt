package com.example.cubesolver

import androidx.compose.ui.graphics.Color
import kotlin.math.sqrt

// Helper function to calculate the average of a list of colors
private fun calculateMeanColor(colorsInCluster: List<Color>): Color {
    if (colorsInCluster.isEmpty()) {
        // Return a default color or handle appropriately if a cluster can be empty
        // This might happen if initial centroids are very poor or K is too high.
        // For this cube solver, K=6 is fixed, and clusters shouldn't be empty
        // after the first assignment if there are colors.
        // Returning black as a fallback, but ideally, clusters should not be empty.
        return Color.Black
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

fun createColorClusters(inputColorLists: List<List<Int>>, maxIterations: Int = 100): CubeColors {

    // turn each int into its respective color
    val facesAsColors: List<List<Color>> = inputColorLists.map { list -> list.map { colorInt -> Color(colorInt) } }
    // flatten list for easier iteration
    val allColorsFlat: List<Color> = facesAsColors.flatten()

    // Get middle color to use as a start for the centroids
    var currentCentroids: MutableList<Color> = facesAsColors.map { faceColors ->
        faceColors[4]
    }.toMutableList()

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
            clusters[nearestCentroidIndex].add(color)
        }

        val newCentroids = mutableListOf<Color>()

        // Set the centroids to be at the mean of each cluster
        for (i in 0 until k) {
            val newCentroid = calculateMeanColor(clusters[i])
            newCentroids.add(newCentroid)
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
        // Get an overflowing cluster
        val overflow = clusters.first { it.size > 9 }
        val centroidIndex = clusters.indexOf(overflow)
        var farthest = 0.0
        var index = -1
        // Find the color that is farthest from the current centroid of the cluster it is in
        for (color in overflow) {
            val distance = color.distanceTo(currentCentroids[centroidIndex])
            if (index != -1 || farthest < distance) {
                index = overflow.indexOf(color)
                farthest = distance
            }
        }
        // Move the color to the closest centroid cluster that has less than 9 colors
        val movingColor = overflow.removeAt(index)
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
        for (colorInt in list){
            // for each color
            val asColor = Color(colorInt)
            // See if a cluster contains the wanted color
            for ((position, cluster) in clusters.withIndex()){
                if (cluster.contains(asColor)){
                    // if so remove the color and add the corresponding index to be returned
                    cluster.remove(asColor)
                    colorIndices[face].add(position)
                    break
                }
            }
        }
    }

    return CubeColors(colorValues, colorIndices)
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

data class CubeColors(val colorValues: List<Color>, val colorIndices: List<List<Int>>)