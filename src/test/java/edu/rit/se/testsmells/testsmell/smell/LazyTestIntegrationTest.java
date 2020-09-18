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
public class LazyTestIntegrationTest {

    private TestSmellDetector testSmellDetector;

    @BeforeEach
    void setUp() {
        testSmellDetector = TestSmellDetector.createTestSmellDetector();

        testSmellDetector.addDetectableSmell(new LazyTest());
    }

    @AfterEach
    void tearDown() {
        testSmellDetector.clear();
        testSmellDetector = null;
    }


    @Test
    public void testLazyTest() throws IOException {

        TestFile file = new TestFile(
                "LazyTestProject",
                // Obtained from https://github.com/jevgeniv/aRevelation/blob/950309c589d55b3fc8c879d548bae2224d558668/src/test/java/com/github/marmaladesky/tests/CryptographerTest.java
                "/LazyTest/src/test/java/com/github/marmaladesky/tests/CryptographerTest.java",
                // Obtained from https://github.com/jevgeniv/aRevelation/blob/950309c589d55b3fc8c879d548bae2224d558668/src/main/java/com/github/marmaladesky/Cryptographer.java
                "/LazyTest/src/main/java/com/github/marmaladesky/Cryptographer.java"
        );
        testSmellDetector.detectSmells(file);
        boolean expectedSmellDetected = false;
        for (AbstractSmell testSmell : file.getTestSmells()) {
            if (testSmell != null) {
                if (testSmell.getSmellName().equals(new LazyTest().getSmellName())) {
                    expectedSmellDetected = testSmell.hasSmell();
                }
            }
        }
        assertTrue(expectedSmellDetected);

    }
}
