package io;

import testsmell.TestFile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Read the input file and build the TestFile objects
 */
public class TestFileCsvReader {
    private final List<TestFile> testFiles = new ArrayList<>();

    public TestFileCsvReader(String filePath) throws IOException {
        try (BufferedReader in = new BufferedReader(new FileReader(filePath))) {
            String str;

            String[] lineItem;
            TestFile testFile;

            while ((str = in.readLine()) != null) {
                // use comma as separator
                lineItem = str.split(",");

                //check if the test file has an associated production file
                if (lineItem.length == 2) {
                    testFile = new TestFile(lineItem[0], lineItem[1], "");
                } else {
                    testFile = new TestFile(lineItem[0], lineItem[1], lineItem[2]);
                }

                testFiles.add(testFile);
            }
        }

    }

    public List<TestFile> getTestFiles() {
        return testFiles;
    }
}
