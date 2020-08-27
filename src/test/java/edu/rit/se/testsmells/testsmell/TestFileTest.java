package edu.rit.se.testsmells.testsmell;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@EnabledOnOs({OS.LINUX, OS.MAC})
class UnixTestFileTest {
    private String fileTest = "commons-lang," +
            "/Users/grano/projects/commons-lang/src/test/java/org/apache/commons/lang3/RandomStringUtilsTest.java," +
            "/Users/grano/projects/commons-lang/src/main/java/org/apache/commons/lang3/RandomStringUtils.java";
    private TestFile testFileUnix;

    @BeforeEach
    void setUp() {
        String[] splits = fileTest.split(",");
        testFileUnix = new TestFile(splits[0], splits[1], splits[2]);
    }

    @Test
    public void testGetProductionFileNameUnix() {
        String oracle = "RandomStringUtils.java";
        String output = testFileUnix.getProductionFileName();
        assertEquals(oracle, output);
    }

    @Test
    public void testGetFileNameUnix() {
        String oracle = "RandomStringUtilsTest.java";
        String output = testFileUnix.getTestFileName();
        assertEquals(oracle, output);
    }

    @Test
    public void testGetRelativeProductionFilePathUnix() {
        String oracle = "src/main/java/org/apache/commons/lang3/RandomStringUtils.java";
        String output = testFileUnix.getRelativeProductionFilePath();
        assertEquals(oracle, output);
    }

    @Test
    public void testGetRelativeTestFilePathUnix() {
        String oracle = "src/test/java/org/apache/commons/lang3/RandomStringUtilsTest.java";
        String output = testFileUnix.getRelativeTestFilePath();
        assertEquals(oracle, output);
    }
}

@EnabledOnOs({OS.WINDOWS})
class WindowsTestFileTest {

    private String fileTestWindows = "myCoolApp," +
            "F:\\Apps\\myCoolApp\\code\\test\\GraphTest.java," +
            "F:\\Apps\\myCoolApp\\code\\src\\Graph.java";
    private TestFile testFileWindows;

    @BeforeEach
    void setUp() {
        String[] split = fileTestWindows.split(",");
        testFileWindows = new TestFile(split[0], split[1], split[2]);
    }

    @Test
    @EnabledOnOs({OS.WINDOWS})
    public void testProductionFileNameWindows() {
        String oracle = "Graph.java";
        String output = testFileWindows.getProductionFileName();
        assertEquals(oracle, output);
    }

    @Test
    @EnabledOnOs({OS.WINDOWS})
    public void testGetFileNameWindows() {
        String oracle = "GraphTest.java";
        String output = testFileWindows.getTestFileName();
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

class TestFileSpecialCasesTest {
    String path;
    String app;

    @BeforeEach
    public void setUp() {
        app = "project";
    }

    @Test
    public void testFilePathWithoutSeparatorTest() {
        path = "file.extension";
        assertThrows(IllegalArgumentException.class, () -> new TestFile(app, path, path));
    }

    @Test
    public void testFileWithoutExtension() {
        path = app + File.separator + "to" + File.separator + "fileWithoutExtension";
        assertThrows(IllegalArgumentException.class, () -> new TestFile(app, path, path));
    }

    @Test
    public void testEmptyAppName() {
        path = "to" + File.separator + "fileWithoutExtension";
        assertThrows(IllegalArgumentException.class, () -> new TestFile("", path, path));
    }
}