package edu.rit.se.testsmells.testsmell.smell;

import edu.rit.se.testsmells.testsmell.AbstractSmell;
import edu.rit.se.testsmells.testsmell.IntegrationTest;
import edu.rit.se.testsmells.testsmell.TestFile;
import edu.rit.se.testsmells.testsmell.TestSmellDetector;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

@IntegrationTest
public class SleepyTestIntegrationTest {

    private TestSmellDetector testSmellDetector;

    @BeforeEach
    void setUp() {
        testSmellDetector = TestSmellDetector.createTestSmellDetector();

        testSmellDetector.addDetectableSmell(new SleepyTest());
    }

    @AfterEach
    void tearDown() {
        testSmellDetector.clear();
        testSmellDetector = null;
    }


    @Test
    public void testSleepyTest() throws IOException {

        TestFile file = new TestFile(
                "SleepyTestProject",
                "/SleepyTest/src/sk/baka/aedict/ResultActivityTest.java",
                ""
        );
        testSmellDetector.detectSmells(file);
        boolean expectedSmellDetected = false;
        for (AbstractSmell testSmell : file.getTestSmells()) {
            if (testSmell != null) {
                if (testSmell.getSmellName().equals(new SleepyTest().getSmellName())) {
                    expectedSmellDetected = testSmell.hasSmell();
                }
            }
        }
        assertTrue(expectedSmellDetected);

    }
}
