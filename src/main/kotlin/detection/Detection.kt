package detection

import testsmell.AbstractSmell
import testsmell.TestFile
import testsmell.TestSmellDetector
import thresholds.Thresholds

/**
 * Runs the detection by exploiting the TestSmellDetector class
 */
class Detection(private val project: String,
                private val pairs: List<TestProductionPair>,
                private val testSmellDetector: TestSmellDetector,
                val threshold: Thresholds) {

    /**
     * Analyze the given pairs and return a list of DetectionResult
     */
    fun detectSmells(getSmellValue: (AbstractSmell) -> Int): List<DetectionResult> {
        val resultList = mutableListOf<DetectionResult>()
        for (pair in pairs) {
            val testFile = TestFile(project, pair.testClassPath, pair.productionClassPath)
            val tempFile: TestFile = testSmellDetector.detectSmells(testFile)

            val smellLists: List<String> = testSmellDetector.testSmellNames
            val smellValues: List<Int> = tempFile.testSmells.map { getSmellValue.invoke(it) }
            val outputs: List<Pair<String, Int>> = smellLists.flatMap { name ->
                smellValues.map { name to it }
            }

            val detectionResult = DetectionResult(
                    application = project,
                    testFileName = tempFile.testFileName,
                    testFilePath = tempFile.testFilePath,
                    productionFilePath = tempFile.productionFilePath,
                    relativeTestFilePath = tempFile.relativeTestFilePath,
                    relativeProductionFilePath = tempFile.relativeProductionFilePath,
                    numberOfTestMethods = testFile.numberOfTestMethods,
                    smellResult = outputs
            )
            resultList.add(detectionResult)
        }
        return resultList
    }
}