package edu.rit.se.testsmells.testsmell;

import edu.rit.se.testsmells.testsmell.smell.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;

@IntegrationTest
public class TestSmellDetectorIntegrationTest {

    private TestSmellDetector testSmellDetector;

    @BeforeEach
    void setUp() {
        testSmellDetector = TestSmellDetector.createTestSmellDetector();

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
        testSmellDetector.addDetectableSmell(new IgnoredTest());
        testSmellDetector.addDetectableSmell(new ResourceOptimism());
        testSmellDetector.addDetectableSmell(new MagicNumberTest());
        testSmellDetector.addDetectableSmell(new DependentTest());
    }

    @AfterEach
    void tearDown() {
        testSmellDetector.clear();
        testSmellDetector = null;
    }

    @Test
    public void testSmellsFreeProject() throws IOException {
        TestFile file = new TestFile(
                "SmellsFreeProject",
                "/Queue/src/test/java/br/ufmg/aserg/victorveloso/queue/QueueTest.java",
                "/Queue/src/main/java/br/ufmg/aserg/victorveloso/queue/Queue.java"
        );
        testSmellDetector.detectSmells(file);

        for (AbstractSmell testSmell : file.getTestSmells()) {
            if (testSmell != null) {
                assertFalse(testSmell.hasSmell(), String.format("Detected smell named %s", testSmell.getSmellName()));
            }
        }
    }
}
