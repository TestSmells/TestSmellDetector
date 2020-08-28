package edu.rit.se.testsmells.testsmell;

import edu.rit.se.testsmells.testsmell.smell.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.fail;

@IntegrationTest
public class TestSmellDetectorIT {
    @Test
    public void testSmellDetection() {
        TestSmellDetector testSmellDetector = initializeSmells();

        String inputFilesCSVIndex = "Queue,/Queue/src/main/java/br/ufmg/aserg/victorveloso/queue/Queue.java,/Queue/src/test/java/br/ufmg/aserg/victorveloso/queue/QueueTest.java";
        List<TestFile> files = readInputTestFiles(inputFilesCSVIndex);
        for (TestFile file : files) {
            try {
                testSmellDetector.detectSmells(file);
            } catch (NullPointerException | IOException e) {
                fail(e.getMessage());
            }
        }
    }

    private List<TestFile> readInputTestFiles(String inputFileContent) {
        TestFile testFile;
        List<TestFile> testFiles = new ArrayList<>();
        for (List<String> entry : splitLinesAndParseCSV(inputFileContent)) {
            testFile = new TestFile(entry.get(0), entry.get(1), entry.get(2));
            testFiles.add(testFile);
        }
        return testFiles;
    }

    private List<List<String>> splitLinesAndParseCSV(String inputFileContent) {
        return Arrays.stream(inputFileContent.split("\n")).map(s -> Arrays.asList(s.split(","))).collect(Collectors.toList());
    }

    private TestSmellDetector initializeSmells() {
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
