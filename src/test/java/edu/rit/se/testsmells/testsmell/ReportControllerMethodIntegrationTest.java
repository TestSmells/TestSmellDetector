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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@IntegrationTest
class ReportControllerMethodIntegrationTest {
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
        sut = new ReportController(csvWriter, Collections.singletonList(ReportController.ReportGranularity.METHOD));
        sut.report(Collections.singletonList(file));
        outputFile = new File(csvWriter.getFilename());
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
    void testReport_METHOD() throws IOException {
        List<String> entries = new BufferedReader(new FileReader(outputFile)).lines().collect(Collectors.toList());


        assertEquals("Element Name,WhileCount,ConditionCount,RedundantCount,AssertCount,IfCount,ExceptionCount,ForeachCount,PrintCount,SwitchCount,MysteryCount,ForCount,VerboseCount,ResourceOptimismCount,ThreadSleepCount,SensitiveCount,MagicNumberCount,Assertion Roulette,Eager Test,Mystery Guest,Sleepy Test,Unknown Test,Redundant Assertion,Dependent Test,Magic Number Test,Conditional Test Logic,EmptyTest,General Fixture,Sensitive Equality,Verbose Test,IgnoredTest,Resource Optimism,Duplicate Assert,Exception Catching Throwing,Print Statement,Lazy Test", entries.get(0));
        assertEquals("com.github.marmaladesky.tests.CryptographerTest.testDecrypt,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true,false,true", entries.get(1));
        assertEquals("com.github.marmaladesky.tests.CryptographerTest.testEncrypt,0,0,0,1,0,1,0,0,0,0,0,0,0,0,0,0,false,true,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true,false,true", entries.get(2));
    }

    @Test
    void testHeader_METHOD() throws IOException {
        List<String> headerEntries = Arrays.asList(new BufferedReader(new FileReader(outputFile)).readLine().split(","));

        assertEquals("Element Name,WhileCount,ConditionCount,RedundantCount,AssertCount,IfCount,ExceptionCount,ForeachCount,PrintCount,SwitchCount,MysteryCount,ForCount,VerboseCount,ResourceOptimismCount,ThreadSleepCount,SensitiveCount,MagicNumberCount,Assertion Roulette,Eager Test,Mystery Guest,Sleepy Test,Unknown Test,Redundant Assertion,Dependent Test,Magic Number Test,Conditional Test Logic,EmptyTest,General Fixture,Sensitive Equality,Verbose Test,IgnoredTest,Resource Optimism,Duplicate Assert,Exception Catching Throwing,Print Statement,Lazy Test", String.join(",", headerEntries)/*, String.join(",",expectedEntries)*/);
    }

    @Test
    void testContent_METHOD_Decrypt() throws IOException {
        assertEquals(19, getMethodSmells().size());

        List<String> contentEntries = Arrays.asList(new BufferedReader(new FileReader(outputFile)).lines().skip(1).findFirst().orElse("").split(","));
        assertEquals(2, contentEntries.stream().filter(e -> e.equals("true")).count()); // "Lazy Test" and "Exception Catching Throwing"
        assertEquals(17, contentEntries.stream().filter(e -> e.equals("false")).count()); // 18 methods smells - 2 detected smells
    }

    @Test
    void testContent_METHOD_Encrypt() throws IOException {
        assertEquals(19, getMethodSmells().size());

        List<String> contentEntries = Arrays.asList(new BufferedReader(new FileReader(outputFile)).lines().skip(2).findFirst().orElse("").split(","));
        assertEquals(3, contentEntries.stream().filter(e -> e.equals("true")).count()); // "Eager Test", "Lazy Test" and "Exception Catching Throwing"
        assertEquals(16, contentEntries.stream().filter(e -> e.equals("false")).count()); // 18 methods smells - 3 detected smells
    }
}

