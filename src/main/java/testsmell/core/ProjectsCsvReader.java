package testsmell.core;

import testsmell.TestFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Read the input file and build the TestFile objects
 */
public class ProjectsCsvReader {
    private final List<TestFile> testFiles = new ArrayList<>();

    public ProjectsCsvReader(File filePath) throws IOException {
        this(Files.readAllLines(filePath.toPath()));
    }
    public ProjectsCsvReader(List<String> lines ) {
        for (String line: lines) {
            testFiles.add(createTestFileFromCsvLine(line));
        }
    }
    public TestFile createTestFileFromCsvLine(String line) {
        // use comma as separator

        String[] lineItem = line.split(",");
        TestFile testFile;
        //check if the test file has an associated production file
        if (lineItem.length == 2) {
            testFile = new TestFile(lineItem[0], lineItem[1]);
        } else {
            testFile = new TestFile(lineItem[0], lineItem[1], lineItem[2]);
        }
        return testFile;
    }

    public List<TestFile> getTestFiles() {
        return testFiles;
    }
}
