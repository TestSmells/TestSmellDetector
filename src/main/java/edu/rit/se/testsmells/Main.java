package edu.rit.se.testsmells;

import edu.rit.se.testsmells.testsmell.AbstractSmell;
import edu.rit.se.testsmells.testsmell.ResultsWriter;
import edu.rit.se.testsmells.testsmell.TestFile;
import edu.rit.se.testsmells.testsmell.TestSmellDetector;
import edu.rit.se.testsmells.testsmell.smell.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        File inputFile = handleCliArgs(args);

        TestSmellDetector testSmellDetector = TestSmellDetector.createTestSmellDetector();

        initializeSmells(testSmellDetector);

        ResultsWriter resultsWriter = initializeOutputFile(testSmellDetector.getTestSmellNames());

        /*
          Iterate through all test files to detect smells and then write the output
        */
        for (TestFile rawFile : readInputTestFiles(inputFile)) {
            System.out.println(getCurrentDateFormatted() + " Processing: " + rawFile.getTestFilePath());
            System.out.println("Processing: " + rawFile.getTestFilePath());

            TestFile smellyFile = testSmellDetector.detectSmells(rawFile);

            writeOutput(resultsWriter, smellyFile);
        }

        System.out.println("end");
    }

    private static void writeOutput(ResultsWriter resultsWriter, TestFile smellyFile) throws IOException {
        List<String> entries = getTestDescriptionEntries(smellyFile);
        for (AbstractSmell smell : smellyFile.getTestSmells()) {
            try {
                entries.add(String.valueOf(smell.hasSmell()));
            } catch (NullPointerException e) {
                entries.add("");
            }
        }
        resultsWriter.writeLine(entries);
    }

    private static List<TestFile> readInputTestFiles(File inputFile) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(inputFile));
        String str;

        String[] lineItem;
        TestFile testFile;
        List<TestFile> testFiles = new ArrayList<>();
        while ((str = in.readLine()) != null) {
            // use comma as separator
            lineItem = str.split(",");

            //check if the test file has an associated production file
            if (lineItem.length == 2) {
                testFile = new TestFile(lineItem[0], lineItem[1], "");
            } else {
                testFile = new TestFile(lineItem[0], lineItem[1], lineItem[2]);
            }

            testFiles.add(testFile);
        }
        return testFiles;
    }

    private static File handleCliArgs(String[] args) {
        assert args != null && args.length > 0 && args[0].isEmpty() : "Please provide the file containing the paths to the collection of test files";
        File inputFile = new File(args[0]);
        assert inputFile.exists() && !inputFile.isDirectory() : "Please provide a valid file containing the paths to the collection of test files";

        return inputFile;
    }

    private static Object getCurrentDateFormatted() {
        return (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());
    }

    private static List<String> getTestDescriptionEntries(TestFile smellyFile) {
        List<String> columnValues = new ArrayList<>();

        columnValues.add(smellyFile.getApp());
        columnValues.add(smellyFile.getTestFileName());
        columnValues.add(smellyFile.getTestFilePath());
        columnValues.add(smellyFile.getProductionFilePath());
        columnValues.add(smellyFile.getRelativeTestFilePath());
        columnValues.add(smellyFile.getRelativeProductionFilePath());

        return columnValues;
    }

    private static ResultsWriter initializeOutputFile(List<String> testSmellNames) throws IOException {
        ResultsWriter resultsWriter = ResultsWriter.createResultsWriter();
        List<String> columnNames = new ArrayList<>();

        columnNames.add("App");
        columnNames.add("TestClass");
        columnNames.add("TestFilePath");
        columnNames.add("ProductionFilePath");
        columnNames.add("RelativeTestFilePath");
        columnNames.add("RelativeProductionFilePath");

        columnNames.addAll(testSmellNames);

        resultsWriter.writeColumnNames(columnNames);

        return resultsWriter;
    }

    private static void initializeSmells(TestSmellDetector testSmellDetector) {
        testSmellDetector.addDetectableSmell(new AssertionRoulette());
        testSmellDetector.addDetectableSmell(new ConditionalTestLogic());
        testSmellDetector.addDetectableSmell(new ConstructorInitialization());
        testSmellDetector.addDetectableSmell(new DefaultTest());
        testSmellDetector.addDetectableSmell(new EmptyTest());
        testSmellDetector.addDetectableSmell(new ExceptionCatchingThrowing());
        testSmellDetector.addDetectableSmell(new GeneralFixture());
        testSmellDetector.addDetectableSmell(new MysteryGuest());
        testSmellDetector.addDetectableSmell(new PrintStatement());
        testSmellDetector.addDetectableSmell(new RedundantAssertion());
        testSmellDetector.addDetectableSmell(new SensitiveEquality());
        testSmellDetector.addDetectableSmell(new VerboseTest());
        testSmellDetector.addDetectableSmell(new SleepyTest());
        testSmellDetector.addDetectableSmell(new EagerTest());
        testSmellDetector.addDetectableSmell(new LazyTest());
        testSmellDetector.addDetectableSmell(new DuplicateAssert());
        testSmellDetector.addDetectableSmell(new UnknownTest());
        testSmellDetector.addDetectableSmell(new IgnoredTest());
        testSmellDetector.addDetectableSmell(new ResourceOptimism());
        testSmellDetector.addDetectableSmell(new MagicNumberTest());
        testSmellDetector.addDetectableSmell(new DependentTest());
    }


}
