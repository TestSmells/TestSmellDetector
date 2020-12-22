package detection

import testsmell.AbstractSmell
import testsmell.TestFile
import testsmell.TestSmellDetector

/**
 * Runs the detection by exploiting the TestSmellDetector class
 */
class Detection(private val project: String,
                private val testClassPath: String,
                private val productionClassPath: String,
                private val testSmellDetector: TestSmellDetector) {

    /**
     * Analyze a given pair and return a DetectionResult
     */
    fun detectSmells(getSmellValue: (AbstractSmell) -> Any): DetectionResult {
        val testFile = TestFile(project, testClassPath, productionClassPath)
        val tempFile: TestFile = testSmellDetector.detectSmells(testFile)

        val smellLists: List<String> = testSmellDetector.testSmellNames
        val smellValues: List<Any> = tempFile.testSmells.map { getSmellValue.invoke(it) }

        val outputs: List<Pair<String, String>> = smellLists.zip(smellValues.map { e -> e.toString() })

        return DetectionResult(
                application = project,
                testFileName = tempFile.testFileName,
                testFilePath = tempFile.testFilePath,
                productionFilePath = tempFile.productionFilePath,
                relativeTestFilePath = tempFile.relativeTestFilePath,
                relativeProductionFilePath = tempFile.relativeProductionFilePath,
                numberOfTestMethods = testFile.numberOfTestMethods,
                smellResult = outputs
        )
    }
}