package edu.rit.se.testsmells.testsmell;

import edu.rit.se.testsmells.testsmell.smell.*;
import org.junit.Ignore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;

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
    public void testBothDuplicateAssertAndRedundantPrint() throws IOException {
        CSVWriter csvWriter = CSVWriter.createResultsWriter();
        ReportController reportCtrl = ReportController.createReportController(csvWriter);

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

        // Method, class, and file should not be filtered
        assertEquals(reportsSize.size(), 3);
        // 1 DuplicateAssert test case + 7 RedundantPrint test case + header
        assertEquals(reportsSize.get(0), 9);
        // 1 DuplicateAssert test class + 1 RedundantPrint test class + header
        assertEquals(reportsSize.get(1), 3);
        // 1 DuplicateAssert test file + 1 RedundantPrint test file + header
        assertEquals(reportsSize.get(2), 3);
    }

    @Disabled
    @Test
    public void testZxingSmells() throws IOException {
        String inputFile = "/zxing/UniqueTestFiles.txt";
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
        for (TestFile file : testFiles) {
            testSmellDetector.detectSmells(file);
        }
    }
}
