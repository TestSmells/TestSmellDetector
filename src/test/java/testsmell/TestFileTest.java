package testsmell;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import static org.junit.jupiter.api.Assertions.*;

class TestFileTest {

    private String fileTest = "commons-lang," +
            "/Users/giograno/projects/commons-lang/src/test/java/org/apache/commons/lang3/RandomStringUtilsTest.java," +
            "/Users/giograno/projects/commons-lang/src/main/java/org/apache/commons/lang3/RandomStringUtils.java";
    private String fileTestWindows = "myCoolApp," +
            "F:\\Apps\\myCoolApp\\code\\test\\GraphTest.java," +
            "F:\\Apps\\myCoolApp\\code\\src\\Graph.java";
    private TestFile testFileUnix;
    private TestFile testFileWindows;

    @BeforeEach
    void setUp() {
        String[] splits = fileTest.split(",");
        testFileUnix = new TestFile(splits[0], splits[1], splits[2]);
        String[] splitW = fileTestWindows.split(",");
        testFileWindows = new TestFile(splitW[0], splitW[1], splitW[2]);
    }

    @Test
    @EnabledOnOs({OS.WINDOWS})
    public void testGetFileNameWindows() {
        String oracle = "GraphTest.java";
        String output = testFileWindows.getTestFileName();
        assertEquals(oracle, output);
    }

    @Test
    @EnabledOnOs({OS.LINUX, OS.MAC})
    public void testGetFileNameUnix() {
        String oracle = "RandomStringUtilsTest.java";
        String output = testFileUnix.getTestFileName();
        assertEquals(oracle, output);
    }

    @Test
    @EnabledOnOs({OS.WINDOWS})
    public void testProductionFileNameWindows() {
        String oracle = "Graph.java";
        String output = testFileWindows.getProductionFileName();
        assertEquals(oracle, output);
    }

    @Test
    @EnabledOnOs({OS.LINUX, OS.MAC})
    public void testGetProductionFileNameUnix() {
        String oracle = "RandomStringUtils.java";
        String output = testFileUnix.getProductionFileName();
        assertEquals(oracle, output);
    }

    @Test
    @EnabledOnOs({OS.LINUX, OS.MAC})
    public void testGetRelativeProductionFilePathUnix() {
        String oracle = "src/main/java/org/apache/commons/lang3/RandomStringUtils.java";
        String output = testFileUnix.getRelativeProductionFilePath();
        assertEquals(oracle, output);
    }

    @Test
    @EnabledOnOs({OS.LINUX, OS.MAC})
    public void testGetRelativeTestFilePathUnix() {
        String oracle = "src/test/java/org/apache/commons/lang3/RandomStringUtilsTest.java";
        String output = testFileUnix.getRelativeTestFilePath();
        assertEquals(oracle, output);
    }

    @Test
    @EnabledOnOs({OS.WINDOWS})
    public void testGetRelativeProductionFilePathWindows() {
        String oracle = "code\\src\\Graph.java";
        String output = testFileWindows.getRelativeProductionFilePath();
        assertEquals(oracle, output);
    }

    @Test
    @EnabledOnOs({OS.WINDOWS})
    public void testGetRelativeTestFilePathWindows() {
        String oracle = "code\\test\\GraphTest.java";
        String output = testFileWindows.getRelativeTestFilePath();
        assertEquals(oracle, output);
    }
}