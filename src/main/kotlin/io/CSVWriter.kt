package io

import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import detection.DetectionResult

class CSVWriter(private val destinationPath: String = "test-smells.csv") {

    private var flag: Boolean = false

    /**
     * Write the results of a detection to the csv file
     */
    fun writeResult(result: DetectionResult) {
        if (!flag) {
            csvWriter().open(destinationPath) {
                val header = listOf("App", "TestClass", "TestFilePath", "ProductionFilePath",
                        "RelativeTestFilePath", "RelativeProductionFilePath", "NumberOfMethods")
                val smells = result.smellResult.map { it.first }
                writeRow(header.plus(smells))
                flag = true
            }
        }
        val toSave = listOf(result.application,
                result.testFileName, result.testFilePath, result.productionFilePath,
                result.relativeTestFilePath, result.relativeProductionFilePath, result.numberOfTestMethods)
        csvWriter().open(destinationPath, append = true) {
            writeRow(toSave.plus(result.smellResult.map { it.second.toString() }))
        }
    }


}