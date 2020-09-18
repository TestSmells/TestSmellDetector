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
public class MysteryGuestIntegrationTest {

    private TestSmellDetector testSmellDetector;

    @BeforeEach
    void setUp() {
        testSmellDetector = TestSmellDetector.createTestSmellDetector();

        testSmellDetector.addDetectableSmell(new MysteryGuest());
    }

    @AfterEach
    void tearDown() {
        testSmellDetector.clear();
        testSmellDetector = null;
    }

    @Test
    public void testMysteryGuest() throws IOException {
        TestFile file = new TestFile(
                "MysteryGuestProject",
                "/MysteryGuest/src/androidTest/java/com/gmail/walles/johan/batterylogger/SystemStateTest.java",
                ""
        );

        testSmellDetector.detectSmells(file);
        boolean expectedSmellDetected = false;
        for (AbstractSmell testSmell : file.getTestSmells()) {
            if (testSmell != null) {
                if (testSmell.getSmellName().equals(new MysteryGuest().getSmellName())) {
                    expectedSmellDetected = testSmell.hasSmell();
                }
            }
        }
        assertTrue(expectedSmellDetected);

    }
}
