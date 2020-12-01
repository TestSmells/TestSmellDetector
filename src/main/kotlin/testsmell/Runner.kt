package testsmell

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.clikt.parameters.types.file
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import io.InputData
import thresholds.DefaultThresholds
import thresholds.SpadiniThresholds
import thresholds.Thresholds
import java.io.File

class DetectorRunner : CliktCommand() {
    private val inputFile: File? by option("-f", "--file", help = "The csv input file").file()
    val thresholds: String by option("-t", "--thresholds", help = "The threshold to use for the detection")
            .choice("default", "spadini").default("default")
    val granularity: String by option("-g", "--granularity", help = "Boolean value of numerical for the detection")
            .choice("boolean", "numerical").default("boolean")

    override fun run() {
        inputFile?.let {
            val inputData: List<InputData> = readInputFile()
            val thresholdStrategy: Thresholds = if (thresholds == "default") DefaultThresholds() else SpadiniThresholds()
            val granularityFunction: ((TestFile) -> Int) = {
                if (granularity == "boolean") {
                    it.numberOfTestMethods
                } else {
                    it.numberOfTestMethods
                }
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