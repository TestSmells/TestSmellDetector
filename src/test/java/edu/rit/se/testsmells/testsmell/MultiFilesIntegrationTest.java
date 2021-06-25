package edu.rit.se.testsmells.testsmell;

import edu.rit.se.testsmells.testsmell.smell.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@IntegrationTest
public class MultiFilesIntegrationTest {

    private TestSmellDetector testSmellDetector;
    private List<File> outputFiles;

    @BeforeEach
    void setUp() {
        outputFiles = new ArrayList<>();
        testSmellDetector = TestSmellDetector.createTestSmellDetector();

        testSmellDetector.addDetectableSmell(new AssertionRoulette());
        testSmellDetector.addDetectableSmell(new ConditionalTestLogic());
        testSmellDetector.addDetectableSmell(new ConstructorInitialization());
        testSmellDetector.addDetectableSmell(new DefaultTest());
        testSmellDetector.addDetectableSmell(new EmptyTest());
        testSmellDetector.addDetectableSmell(new ExceptionCatchingThrowing());
        testSmellDetector.addDetectableSmell(new GeneralFixture());
        testSmellDetector.addDetectableSmell(new PrintStatement());
        testSmellDetector.addDetectableSmell(new RedundantAssertion());
        testSmellDetector.addDetectableSmell(new MysteryGuest());
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

    @AfterEach
    void tearDown() {
        testSmellDetector.clear();
        testSmellDetector = null;
        outputFiles.forEach(File::delete);
    }

    @Test
    public void testTwoMethodOrientedSmells() throws IOException {
        List<TestFile> testFiles = Arrays.asList(
                new TestFile(
                        "RedundantPrintProject",
                        "/RedundantPrint/src/test/java/org/hwyl/sexytopo/control/util/Space3DTransformerTest.java",
                        ""
                ),
                new TestFile(
                        "DuplicateAssertProject",
                        "/DuplicateAssert/src/test/java/org/openbmap/utils/XmlSanitizerTest.java",
                        ""
                ));

        List<Integer> reportsSize = detectAndReportLOC(testFiles);

        // Method, class, and file should not be filtered
        assertEquals(3, reportsSize.size());
        // 1 DuplicateAssert test case + 7 RedundantPrint test case + header
        assertEquals(9, reportsSize.get(0));
        // 1 DuplicateAssert test class + 1 RedundantPrint test class + header
        assertEquals(3, reportsSize.get(1));
        // 1 DuplicateAssert test file + 1 RedundantPrint test file + header
        assertEquals(3, reportsSize.get(2));
    }

    @Test
    public void testTwoClassOrientedSmells() throws IOException {
        List<TestFile> testFiles = Arrays.asList(
                new TestFile(
                        "DefaultTestProject",
                        "/DefaultTest/src/test/java/com/app/missednotificationsreminder/ExampleUnitTest.java",
                        ""
                ),
                new TestFile(
                        "ConstructorInitializationProject",
                        "/ConstructorInitialization/src/test/java/org/briarproject/bramble/crypto/TagEncodingTest.java",
                        ""
                ));

        List<Integer> reportsSize = detectAndReportLOC(testFiles);

        // Method, class, and file should not be filtered
        assertEquals(3, reportsSize.size());
        // 1 DuplicateAssert test case + 7 RedundantPrint test case + header
        assertEquals(6, reportsSize.get(0));
        // 10 DefaultTest test classes (1 top level + 9 inner classes) + 1 ConstructorInitialization test class + header
        assertEquals(11, reportsSize.get(1));
        // 1 DuplicateAssert test file + 1 RedundantPrint test file + header
        assertEquals(3, reportsSize.get(2));
    }

    @Test
    public void testBothClassAndMethodOrientedSmells() throws IOException {
        List<TestFile> testFiles = Arrays.asList(
                new TestFile(
                        "DefaultTestProject",
                        "/DefaultTest/src/test/java/com/app/missednotificationsreminder/ExampleUnitTest.java",
                        ""
                ),
                new TestFile(
                        "DuplicateAssertProject",
                        "/DuplicateAssert/src/test/java/org/openbmap/utils/XmlSanitizerTest.java",
                        ""
                ));

        List<Integer> reportsSize = detectAndReportLOC(testFiles);

        // Method, class, and file should not be filtered
        assertEquals(3, reportsSize.size());
        // 1 DuplicateAssert test case + 7 RedundantPrint test case + header
        assertEquals(4, reportsSize.get(0));
        // 10 DefaultTest test classes (1 top level + 9 inner classes) + 1 DuplicateAssert test class + header
        assertEquals(11, reportsSize.get(1));
        // 1 DuplicateAssert test file + 1 RedundantPrint test file + header
        assertEquals(3, reportsSize.get(2));
    }

    @Test
    public void testBothClassAndMethodOrientedSmells_csvColumns() throws IOException {
        List<TestFile> testFiles = Arrays.asList(
                new TestFile(
                        "DefaultTestProject",
                        "/DefaultTest/src/test/java/com/app/missednotificationsreminder/ExampleUnitTest.java",
                        ""
                ),
                new TestFile(
                        "DuplicateAssertProject",
                        "/DuplicateAssert/src/test/java/org/openbmap/utils/XmlSanitizerTest.java",
                        ""
                ));

        detectAndReportLOC(testFiles);

        assertEquals(3, outputFiles.size(), "Not generated all three reports");

        String expectedMethodHeader = "Element Name,WhileCount,ConditionCount,RedundantCount,BadAssertCount,TotalAssertCount,IfCount,ExceptionCount,ForeachCount,PrintCount,SwitchCount,MysteryCount,ForCount,VerboseCount,ResourceOptimismCount,ThreadSleepCount,SensitiveCount,MagicNumberCount,Assertion Roulette,Mystery Guest,Sleepy Test,Unknown Test,Redundant Assertion,Dependent Test,Magic Number Test,Conditional Test Logic,EmptyTest,General Fixture,Sensitive Equality,Verbose Test,IgnoredTest,Resource Optimism,Duplicate Assert,Exception Catching Throwing,Print Statement";
        String expectedFileHeader = "App,ProductionFileName,TestFilePath,TestFileName,RelativeProductionFilePath,RelativeTestFilePath,ProductionFilePath,Assertion Roulette,Conditional Test Logic,Constructor Initialization,Default Test,EmptyTest,Exception Catching Throwing,General Fixture,Print Statement,Redundant Assertion,Mystery Guest,Sensitive Equality,Verbose Test,Sleepy Test,Eager Test,Lazy Test,Duplicate Assert,Unknown Test,IgnoredTest,Resource Optimism,Magic Number Test,Dependent Test";
        String expectedClassHeader = "Element Name,IgnoredTest,Constructor Initialization,Default Test";

        List<Long> expectedCounts = Stream.of(expectedMethodHeader,expectedClassHeader,expectedFileHeader).map(s-> Arrays.stream(s.split(",")).count()).collect(Collectors.toList());

        for (int i = 0; i < outputFiles.size(); i++) {
            BufferedReader reader = new BufferedReader(new FileReader(outputFiles.get(i)));
            List<Long> colCounts = reader.lines().map(line -> Arrays.stream(line.split(",")).count()).distinct().collect(Collectors.toList());
            assertEquals(1, colCounts.size(), "Lines with different number of columns");
            Long[] expected = {expectedCounts.get(i)};
            assertArrayEquals(expected,colCounts.toArray());
        }
    }

    private List<Integer> detectAndReportLOC(List<TestFile> testFiles) throws IOException {
        CSVWriter csvWriter = CSVWriter.createResultsWriter();
        ReportController reportCtrl = ReportController.createReportController(csvWriter);

        for (TestFile file : testFiles) {
            testSmellDetector.detectSmells(file);
        }

        reportCtrl.report(testFiles);

        outputFiles = Stream.of(
                ReportController.ReportGranularity.METHOD,
                ReportController.ReportGranularity.CLASS,
                ReportController.ReportGranularity.FILE
        )
                .map(granularity -> granularity.toString() + "_" + csvWriter.getSuffix())
                .map(File::new)
                .collect(Collectors.toList());
        List<Integer> reportsSize = outputFiles.stream()
                .filter(File::exists).filter(f -> !f.isDirectory())
                .map(file -> {
                    try {
                        return (int) new BufferedReader(new FileReader(file)).lines().count();
                    } catch (FileNotFoundException e) { // Never occurs
                        e.printStackTrace();
                        return 0;
                    }
                })
                .collect(Collectors.toList());
        return reportsSize;
    }
}
