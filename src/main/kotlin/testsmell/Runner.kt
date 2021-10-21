package testsmell

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.clikt.parameters.types.file
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import detection.Detection
import detection.DetectionResult
import io.CSVWriter
import io.InputData
import thresholds.DefaultThresholds
import thresholds.SpadiniThresholds
import thresholds.Thresholds
import java.io.File

class DetectorRunner : CliktCommand() {
    private val inputFile: File? by option("-f", "--file", help = "The csv input file").file()
    val thresholds: String by option("-t", "--thresholds", help = "The threshold to use for the detection")
            .choice("default", "spadini").default("default")
    private val granularity: String by option("-g", "--granularity", help = "Boolean value of numerical for the detection")
            .choice("boolean", "numerical").default("boolean")
    private val output: String by option("-o", "--output", help = "").default("test-smells.csv")

    override fun run() {
        val thresholdStrategy: Thresholds = if (thresholds == "default") DefaultThresholds() else SpadiniThresholds()
        val granularityFunction: ((AbstractSmell) -> Any) = {
            if (granularity == "boolean") {
                it.hasSmell()
            } else {
                it.numberOfSmellyTests
            }
        }

        inputFile?.let {
            val inputData: List<InputData> = readInputFile()
            val writer = CSVWriter(output)
            for (input in inputData) {
                val detection = Detection(
                        project = input.application,
                        testClassPath = input.testPath,
                        productionClassPath = input.productionPath,
                        testSmellDetector = TestSmellDetector(thresholdStrategy)
                )
                val detectedSmell: DetectionResult = detection.detectSmells(granularityFunction)
                writer.writeResult(detectedSmell)
            }
        } ?: println("No input file specified")
    }

    /**
     * Reads the input file and returns a list of the files to analyze
     */
    private fun readInputFile(): List<InputData> {
        val rows: List<List<String>> = csvReader().readAll(inputFile!!)
        val inputData = mutableListOf<InputData>()
        for (row in rows)
            inputData.add(InputData(application = row[0], testPath = row[1], productionPath = row[2]))
        return inputData
    }
}

fun main(args: Array<String>) = DetectorRunner().main(args)