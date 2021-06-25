package edu.rit.se.testsmells.testsmell;

import edu.rit.se.testsmells.testsmell.smell.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@IntegrationTest
class ReportControllerFileIntegrationTest {
    private File outputFile;
    private TestFile file;
    private ReportController sut;
    private CSVWriter csvWriter;
    private List<AbstractSmell> smells = Arrays.asList(new AssertionRoulette(), new ConditionalTestLogic(), new ConstructorInitialization(), new DefaultTest(), new EmptyTest(), new ExceptionCatchingThrowing(), new GeneralFixture(), new MysteryGuest(), new PrintStatement(), new RedundantAssertion(), new SensitiveEquality(), new VerboseTest(), new SleepyTest(), new EagerTest(), new LazyTest(), new DuplicateAssert(), new UnknownTest(), new IgnoredTest(), new ResourceOptimism(), new MagicNumberTest(), new DependentTest());
    private String appName = "LazyTest";
    private String testFilePath = "/LazyTest/src/test/java/com/github/marmaladesky/tests/CryptographerTest.java";
    private String productionFilePath = "/LazyTest/src/main/java/com/github/marmaladesky/Cryptographer.java";

    @BeforeEach
    void setUp() throws IOException {
        TestSmellDetector testSmellDetector = TestSmellDetector.createTestSmellDetector();
        smells.forEach(testSmellDetector::addDetectableSmell);
        file = new TestFile(appName, testFilePath, productionFilePath);
        csvWriter = CSVWriter.createResultsWriter();
        testSmellDetector.detectSmells(file);
        sut = new ReportController(csvWriter, Collections.singletonList(ReportController.ReportGranularity.FILE));
        sut.report(Collections.singletonList(file));
        outputFile = new File(csvWriter.getFilename());
        assertTrue(outputFile.exists(), "Output file missing!");
    }

    @AfterEach
    void tearDown() {
        //noinspection ResultOfMethodCallIgnored
        outputFile.delete();
    }

    @Test
    void testReportExists() {
        assertTrue(outputFile.exists(), "Output file missing!");
    }

    @Test
    void testReportDoestNotThrow() {
        sut = new ReportController(csvWriter, Collections.singletonList(ReportController.ReportGranularity.FILE));
        assertDoesNotThrow(() -> sut.report(Collections.singletonList(file)));
        outputFile = new File(csvWriter.getFilename());
    }

    @Test
    void testHeader_FILE() throws IOException {
        List<String> headerEntries = Arrays.asList(new BufferedReader(new FileReader(outputFile)).readLine().split(","));

        List<String> expectedEntries = new ArrayList<>(Arrays.asList("App", "ProductionFileName", "TestFilePath", "TestFileName", "RelativeProductionFilePath", "RelativeTestFilePath", "ProductionFilePath"));
        List<String> smellsName = smells.stream().map(AbstractSmell::getSmellName).collect(Collectors.toList());
        expectedEntries.addAll(smellsName);

        assertIterableEquals(headerEntries, expectedEntries);
    }

    @Test
    void testContent_FILE() throws IOException {
        List<String> contentEntries = Arrays.asList(new BufferedReader(new FileReader(outputFile)).lines().skip(1).findFirst().orElse("").split(","));

        List<String> expectedEntries = new ArrayList<>(Arrays.asList(appName, "Cryptographer.java", testFilePath, "CryptographerTest.java", "src/main/java/com/github/marmaladesky/Cryptographer.java", "src/test/java/com/github/marmaladesky/tests/CryptographerTest.java", productionFilePath));

        List<String> expectedSmells = Arrays.asList("Lazy Test", "Eager Test", "Exception Catching Throwing");
        List<String> hasSmell = smells.stream().map(x -> expectedSmells.contains(x.getSmellName()) ? "true" : "false").collect(Collectors.toList());

        expectedEntries.addAll(hasSmell);

        assertIterableEquals(expectedEntries, contentEntries);
    }

    @Test
    void testConsistentColumns() throws IOException {
        BufferedReader content = new BufferedReader(new FileReader(outputFile));
        int nCols = content.readLine().split(",").length;

        assertEquals(nCols, content.readLine().split(",").length);
        assertEquals(nCols, 7 + smells.size());
    }
}
