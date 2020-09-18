package edu.rit.se.testsmells.testsmell;

import edu.rit.se.testsmells.testsmell.smell.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@IntegrationTest
public class TestSmellDetectorIT {

    private TestSmellDetector testSmellDetector;

    @BeforeEach
    void setUp() {
        testSmellDetector = initializeSmells();
    }

    @Test
    public void testAssertionRoulette() {
        List<TestFile> files = Collections.singletonList(new TestFile(
                "AssertionRouletteProject",
                "/AssertionRoulette/src/test/com/madgag/agit/AssertionRouletteTest.java",
                ""
        ));

        detectSmells(files);
        boolean expectedSmellDetected = false;
        for (TestFile file : files) {
            for (AbstractSmell testSmell : file.getTestSmells()) {
                if (testSmell != null) {
                    if (testSmell.getSmellName().equals(new AssertionRoulette().getSmellName())) {
                        expectedSmellDetected = testSmell.hasSmell();
                    }
                }
            }
        }
        assertTrue(expectedSmellDetected);

    }

    @Test
    public void testSmellsFreeProject() {
        List<TestFile> files = Collections.singletonList(new TestFile(
                "SmellsFreeProject",
                "/Queue/src/test/java/br/ufmg/aserg/victorveloso/queue/QueueTest.java",
                "/Queue/src/main/java/br/ufmg/aserg/victorveloso/queue/Queue.java"
        ));
        detectSmells(files);

        for (TestFile file : files) {
            for (AbstractSmell testSmell : file.getTestSmells()) {
                if (testSmell != null) {
                    assertFalse(testSmell.hasSmell(), String.format("Detected smell named %s", testSmell.getSmellName()));
                }
            }
        }
    }

    private void detectSmells(List<TestFile> files) {
        for (TestFile file : files) {
            testSmellDetector.detectSmells(file);
        }
    }

    private TestSmellDetector initializeSmells() {
        TestSmellDetector testSmellDetector = TestSmellDetector.createTestSmellDetector();

        testSmellDetector.addDetectableSmell(new AssertionRoulette());
        testSmellDetector.addDetectableSmell(new ConditionalTestLogic());

        testSmellDetector.addDetectableSmell(new ConstructorInitialization()); // Constructor Initialization gives false positives
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
        //testSmellDetector.addDetectableSmell(new IgnoredTest());
        testSmellDetector.addDetectableSmell(new ResourceOptimism());
        testSmellDetector.addDetectableSmell(new MagicNumberTest());
        testSmellDetector.addDetectableSmell(new DependentTest());

        return testSmellDetector;
    }
}
