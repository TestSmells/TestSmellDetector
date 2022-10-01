package testsmell.core;

import testsmell.AbstractSmell;
import testsmell.ResultsWriter;
import testsmell.TestFile;
import testsmell.TestSmellDetector;
import thresholds.DefaultThresholds;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Iterates through all given test files to detect smells
 * Can save output to a file
 */
public class SmellDetectionWriter {
    private static final DateFormat dateFormatForOutput = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

    private final TestSmellDetector testSmellDetector ;
    private final List<TestFile> monitoredJavaFiles;

    public SmellDetectionWriter(List<TestFile> testFiles) {
       this(testFiles, new TestSmellDetector(new DefaultThresholds()));
    }
    public SmellDetectionWriter(List<TestFile> testFiles, TestSmellDetector testSmellDetector ) {
        this.monitoredJavaFiles = testFiles;
        this.testSmellDetector = testSmellDetector;
    }

    private ResultsWriter createStartingResultsWriter()  throws IOException{
        ResultsWriter resultsWriter = ResultsWriter.createResultsWriter();
        List<String> columnNames;
        columnNames = testSmellDetector.getTestSmellNames();
        columnNames.add(0, "App");
        columnNames.add(1, "TestClass");
        columnNames.add(2, "TestFilePath");
        columnNames.add(3, "ProductionFilePath");
        columnNames.add(4, "RelativeTestFilePath");
        columnNames.add(5, "RelativeProductionFilePath");
        columnNames.add(6, "NumberOfMethods");
        resultsWriter.writeColumnName(columnNames);
        return resultsWriter;
    }

    public ResultsWriter createProcessedResultsWriter() throws IOException {

        ResultsWriter resultsWriter = createStartingResultsWriter();

        /*
          Iterate through all test files to detect smells
        */

        for (TestFile file : monitoredJavaFiles) {
            resultsWriter.writeLine(createColumnValues(file));
        }
        return resultsWriter;
    }

    private List<String> createColumnValues(TestFile file) throws IOException{
        Date date = new Date();
        System.out.println(dateFormatForOutput.format(date) + " Processing: " + file.getTestFilePath());
        System.out.println("Processing: " + file.getTestFilePath());

        //detect smells
        TestFile tempFile = testSmellDetector.detectSmells(file);

        //write output
        List<String> columnValues = new ArrayList<>();
        columnValues.add(file.getApp());
        columnValues.add(file.getTestFileName());
        columnValues.add(file.getTestFilePath());
        columnValues.add(file.getProductionFilePath());
        columnValues.add(file.getRelativeTestFilePath());
        columnValues.add(file.getRelativeProductionFilePath());
        columnValues.add(String.valueOf(file.getNumberOfTestMethods()));
        for (AbstractSmell smell : tempFile.getTestSmells()) {
            try {
                columnValues.add(String.valueOf(smell.getNumberOfSmellyTests()));
            } catch (NullPointerException e) {
                columnValues.add("");
            }
        }
        return columnValues;
    }
}
