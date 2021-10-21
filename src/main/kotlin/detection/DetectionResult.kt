package detection

/**
 * The possible values we can obtain from a detection run on a given class
 */
data class DetectionResult(
        val application: String,
        val testFileName: String,
        val testFilePath: String,
        val productionFilePath: String,
        val relativeTestFilePath: String,
        val relativeProductionFilePath: String,
        val numberOfTestMethods: Int,
        val smellResult: List<Pair<String, String>>
)
