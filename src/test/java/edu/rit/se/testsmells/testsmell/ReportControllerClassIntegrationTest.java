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
class ReportControllerClassIntegrationTest {
    private File outputFile;
    private TestFile file;
    private ReportController sut;
    private CSVWriter csvWriter;
    private List<AbstractSmell> smells = Arrays.asList(new AssertionRoulette(), new ConditionalTestLogic(), new ConstructorInitialization(), new DefaultTest(), new EmptyTest(), new ExceptionCatchingThrowing(), new GeneralFixture(), new MysteryGuest(), new PrintStatement(), new RedundantAssertion(), new SensitiveEquality(), new VerboseTest(), new SleepyTest(), new EagerTest(), new LazyTest(), new DuplicateAssert(), new UnknownTest(), new IgnoredTest(), new ResourceOptimism(), new MagicNumberTest(), new DependentTest());
    private String appName = "ConstructorInitializationProject";
    private String testFilePath = "/ConstructorInitialization/src/test/java/org/briarproject/bramble/crypto/TagEncodingTest.java";
    private String productionFilePath = "";

    @BeforeEach
    void setUp() throws IOException {
        TestSmellDetector testSmellDetector = TestSmellDetector.createTestSmellDetector();
        smells.forEach(testSmellDetector::addDetectableSmell);
        file = new TestFile(appName, testFilePath, productionFilePath);
        csvWriter = CSVWriter.createResultsWriter();
        testSmellDetector.detectSmells(file);
    }

    @AfterEach
    void tearDown() {
        //noinspection ResultOfMethodCallIgnored
        outputFile.delete();
    }


    @Test
    void testCLASSOutput() throws IOException {
        List<ReportController.ReportGranularity> outputs = Arrays.asList(ReportController.ReportGranularity.CLASS);
        sut = new ReportController(csvWriter, outputs);
        sut.report(Collections.singletonList(file));

        outputFile = new File("CLASS_" + csvWriter.getSuffix());
        assertTrue(outputFile.exists(), "Class output file missing!");
        List<String> fileContent = new BufferedReader(new FileReader(outputFile)).lines().collect(Collectors.toList());
        assertEquals(2, fileContent.size(), "File with unexpected size received! File's content is: " + System.lineSeparator() + String.join(System.lineSeparator(), fileContent) + System.lineSeparator());

    }
}
