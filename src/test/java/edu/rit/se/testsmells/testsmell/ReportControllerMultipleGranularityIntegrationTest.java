package edu.rit.se.testsmells.testsmell;

import edu.rit.se.testsmells.testsmell.smell.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@IntegrationTest
class ReportControllerMultipleGranularityIntegrationTest {
    private List<File> outputFiles;
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
        List<ReportController.ReportGranularity> outputs = Arrays.asList(ReportController.ReportGranularity.METHOD, ReportController.ReportGranularity.CLASS, ReportController.ReportGranularity.FILE);
        sut = new ReportController(csvWriter, outputs);
        sut.report(Collections.singletonList(file));
        outputFiles = outputs.stream().map(granularity -> granularity.toString() + "_" + csvWriter.getSuffix()).map(File::new).collect(Collectors.toList());
    }

    @AfterEach
    void tearDown() {
        //noinspection ResultOfMethodCallIgnored
        outputFiles.forEach(File::delete);
    }

    @Test
    void testMultipleOutput_METHOD() throws IOException {
        File outputFile = outputFiles.get(0);
        assertTrue(outputFile.exists(), "Method output file missing!");
        List<String> fileContent = new BufferedReader(new FileReader(outputFile)).lines().collect(Collectors.toList());
        assertEquals(3, fileContent.size(), "File with unexpected size received! File's content is: " + System.lineSeparator() + String.join(System.lineSeparator(), fileContent) + System.lineSeparator());
    }

    @Test
    void testMultipleOutput_CLASS() throws IOException {
        File outputFile = outputFiles.get(1);
        assertTrue(outputFile.exists(), "Class output file missing!");
        List<String> fileContent = new BufferedReader(new FileReader(outputFile)).lines().collect(Collectors.toList());
        assertEquals(2, fileContent.size(), "File with unexpected size received! File's content is: " + System.lineSeparator() + String.join(System.lineSeparator(), fileContent) + System.lineSeparator());

    }

    @Test
    void testMultipleOutput_FILE() throws IOException {
        File outputFile = outputFiles.get(2);
        assertTrue(outputFile.exists(), "File output file missing!");
        List<String> fileContent = new BufferedReader(new FileReader(outputFile)).lines().collect(Collectors.toList());
        assertEquals(2, fileContent.size(), "File with unexpected size received! File's content is: " + System.lineSeparator() + String.join(System.lineSeparator(), fileContent) + System.lineSeparator());


    }
}
