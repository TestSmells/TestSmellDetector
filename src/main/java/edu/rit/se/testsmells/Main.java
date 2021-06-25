package edu.rit.se.testsmells;

import edu.rit.se.testsmells.testsmell.CSVWriter;
import edu.rit.se.testsmells.testsmell.ReportController;
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

        TestSmellDetector testSmellDetector = initializeSmells();

        List<TestFile> files = readInputTestFiles(inputFile);
        CSVWriter csvWriter = CSVWriter.createResultsWriter();
        ReportController reportCtrl = ReportController.createReportController(csvWriter);

        for (TestFile file : files) {
            System.out.println(getCurrentDateFormatted() + " Processing: " + file.getTestFilePath());
            testSmellDetector.detectSmells(file);
        }

        reportCtrl.report(files);

        System.out.println("end");
    }

    private static Object getCurrentDateFormatted() {
        return (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());
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

    private static TestSmellDetector initializeSmells() {
        TestSmellDetector testSmellDetector = TestSmellDetector.createTestSmellDetector();

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

        return testSmellDetector;
    }


}
