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
public class EmptyTestIntegrationTest {

    private TestSmellDetector testSmellDetector;

    @BeforeEach
    void setUp() {
        testSmellDetector = TestSmellDetector.createTestSmellDetector();

        testSmellDetector.addDetectableSmell(new EmptyTest());
    }

    @AfterEach
    void tearDown() {
        testSmellDetector.clear();
        testSmellDetector = null;
    }


    @Test
    public void testEmptyTest() throws IOException {

        TestFile file = new TestFile(
                "EmptyTestProject",
                "/EmptyTest/src/androidTest/java/com/actisec/clipcaster/parser/LastPassParserTest.java",
                ""
        );
        testSmellDetector.detectSmells(file);
        boolean expectedSmellDetected = false;
        for (AbstractSmell testSmell : file.getTestSmells()) {
            if (testSmell != null) {
                if (testSmell.getSmellName().equals(new EmptyTest().getSmellName())) {
                    expectedSmellDetected = testSmell.hasSmell();
                }
            }
        }
        assertTrue(expectedSmellDetected);

    }
}
