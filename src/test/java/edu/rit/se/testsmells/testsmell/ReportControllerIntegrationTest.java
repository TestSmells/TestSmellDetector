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
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@IntegrationTest
public class ReportControllerIntegrationTest {
    File outputFile;
    TestFile file;
    ReportController sut;
    ResultsWriter resultsWriter;
    List<AbstractSmell> smells = Arrays.asList(new AssertionRoulette(), new ConditionalTestLogic(), new ConstructorInitialization(), new DefaultTest(), new EmptyTest(), new ExceptionCatchingThrowing(), new GeneralFixture(), new MysteryGuest(), new PrintStatement(), new RedundantAssertion(), new SensitiveEquality(), new VerboseTest(), new SleepyTest(), new EagerTest(), new LazyTest(), new DuplicateAssert(), new UnknownTest(), new IgnoredTest(), new ResourceOptimism(), new MagicNumberTest(), new DependentTest());
    String appName = "LazyTest";
    String testFilePath = "/LazyTest/src/test/java/com/github/marmaladesky/tests/CryptographerTest.java";
    String productionFilePath = "/LazyTest/src/main/java/com/github/marmaladesky/Cryptographer.java";

    @BeforeEach
    public void setUp() throws IOException {
        TestSmellDetector testSmellDetector = TestSmellDetector.createTestSmellDetector();
        smells.forEach(testSmellDetector::addDetectableSmell);
        file = new TestFile(appName, testFilePath, productionFilePath);
        resultsWriter = ResultsWriter.createResultsWriter();
        resultsWriter.writeCSVHeader(testSmellDetector, file);
        testSmellDetector.detectSmells(file);
        outputFile = new File(resultsWriter.getOutputFile());
    }

    @AfterEach
    public void tearDown() {
        outputFile.delete();
    }

    @Test
    public void testReport() throws IOException {
        sut = new ReportController(resultsWriter, Arrays.asList(ReportController.ReportGranularity.FILE));
        assertDoesNotThrow(() -> sut.report(Arrays.asList(file)));
    }

    @Test
    public void testHeader() throws IOException {
        sut = new ReportController(resultsWriter, Arrays.asList(ReportController.ReportGranularity.FILE));
        sut.report(Arrays.asList(file));
        assertTrue(outputFile.exists(), "Output file missing!");

        List<String> headerEntries = Arrays.asList(new BufferedReader(new FileReader(outputFile)).readLine().split(","));

        List<String> expectedEntries = new ArrayList<>(Arrays.asList("App", "ProductionFileName", "TestFilePath", "TestFileName", "RelativeProductionFilePath", "RelativeTestFilePath", "ProductionFilePath"));
        List<String> smellsName = smells.stream().map(AbstractSmell::getSmellName).collect(Collectors.toList());
        expectedEntries.addAll(smellsName);

        assertIterableEquals(headerEntries, expectedEntries);
    }

    @Test
    public void testContent_FILE() throws IOException {
        sut = new ReportController(resultsWriter, Arrays.asList(ReportController.ReportGranularity.FILE));
        sut.report(Arrays.asList(file));
        assertTrue(outputFile.exists(), "Output file missing!");

        List<String> contentEntries = Arrays.asList(new BufferedReader(new FileReader(outputFile)).lines().skip(1).findFirst().get().split(","));

        List<String> expectedEntries = new ArrayList<>(Arrays.asList(appName, "Cryptographer.java", testFilePath, "CryptographerTest.java", "src/main/java/com/github/marmaladesky/Cryptographer.java", "src/test/java/com/github/marmaladesky/tests/CryptographerTest.java", productionFilePath));

        List<String> expectedSmells = Arrays.asList("Lazy Test", "Eager Test", "Exception Catching Throwing");
        List<String> hasSmell = smells.stream().map(x -> expectedSmells.contains(x.getSmellName()) ? "true" : "false").collect(Collectors.toList());

        expectedEntries.addAll(hasSmell);

        assertIterableEquals(expectedEntries, contentEntries);
    }

    List<AbstractSmell> getMethodSmells() {
        List<AbstractSmell> methodSmells = new ArrayList<>();
        for (AbstractSmell smell : file.getTestSmells()) {
            long nTestMethods = smell.getSmellyElements().stream().filter(elem -> elem instanceof TestMethod).count();
            if (nTestMethods != 0) {
                methodSmells.add(smell);
            }
        }
        return methodSmells;
    }

    @Test
    public void testContent_METHOD_Decrypt() throws IOException {
        sut = new ReportController(resultsWriter, Arrays.asList(ReportController.ReportGranularity.METHOD));
        sut.report(Arrays.asList(file));

        assertTrue(outputFile.exists(), "Output file missing!");

        assertEquals(17, getMethodSmells().size());

        List<String> contentEntries = Arrays.asList(new BufferedReader(new FileReader(outputFile)).lines().skip(1).findFirst().get().split(","));
        assertEquals(2, contentEntries.stream().filter(e -> e.equals("true")).count()); // "Lazy Test" and "Exception Catching Throwing"
        assertEquals(15, contentEntries.stream().filter(e -> e.equals("false")).count()); // 17 methods smells - 2 detected smells
    }

    @Test
    public void testContent_METHOD_Encrypt() throws IOException {
        sut = new ReportController(resultsWriter, Arrays.asList(ReportController.ReportGranularity.METHOD));
        sut.report(Arrays.asList(file));

        assertTrue(outputFile.exists(), "Output file missing!");

        assertEquals(17, getMethodSmells().size());

        List<String> contentEntries = Arrays.asList(new BufferedReader(new FileReader(outputFile)).lines().skip(2).findFirst().get().split(","));
        assertEquals(3, contentEntries.stream().filter(e -> e.equals("true")).count()); // "Eager Test", "Lazy Test" and "Exception Catching Throwing"
        assertEquals(14, contentEntries.stream().filter(e -> e.equals("false")).count()); // 17 methods smells - 3 detected smells
    }

    @Test
    public void testConsistentColumns() throws IOException {
        sut = new ReportController(resultsWriter, Arrays.asList(ReportController.ReportGranularity.FILE));
        sut.report(Arrays.asList(file));
        assertTrue(outputFile.exists(), "Output file missing!");

        BufferedReader content = new BufferedReader(new FileReader(outputFile));
        int nCols = content.readLine().split(",").length;

        assertEquals(nCols, content.readLine().split(",").length);
        assertEquals(nCols, 7 + smells.size());
    }
}
