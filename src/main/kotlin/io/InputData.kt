package io

/**
 * Represents the information we need in input to perform the analysis
 */
data class InputData(
        val application: String,
        val testPath: String,
        val productionPath: String
)
