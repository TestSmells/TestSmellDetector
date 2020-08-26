package edu.rit.se.testsmells.testsmell;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import org.apache.commons.lang3.StringUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TestSmellDetector {

    private List<AbstractSmell> testSmells;

    /**
     * Instantiates the various test smell analyzer classes and loads the objects into an List
     */
    public TestSmellDetector() {
        testSmells = new ArrayList<>();
    }

    public void addDetectableSmell(AbstractSmell smell) {
        testSmells.add(smell);
    }

    /**
     * Factory method that provides a new instance of the TestSmellDetector
     *
     * @return new TestSmellDetector instance
     */
    public static TestSmellDetector createTestSmellDetector() {
        return new TestSmellDetector();
    }

    /**
     * Provides the names of the smells that are being checked for in the code
     *
     * @return list of smell names
     */
    public List<String> getTestSmellNames() {
        return testSmells.stream().map(AbstractSmell::getSmellName).collect(Collectors.toList());
    }

    /**
     * Loads the java source code file into an AST and then analyzes it for the existence of the different types of test smells
     */
    public TestFile detectSmells(TestFile testFile) throws IOException {
        CompilationUnit testFileCompilationUnit = parseIntoCompilationUnit(testFile.getTestFilePath());

        CompilationUnit productionFileCompilationUnit = parseIntoCompilationUnit(testFile.getProductionFilePath());

        for (AbstractSmell smell : testSmells) {
            try {
                smell.runAnalysis(testFileCompilationUnit, productionFileCompilationUnit, testFile.getTestFileNameWithoutExtension(), testFile.getProductionFileNameWithoutExtension());
                testFile.addDetectedSmell(smell);
                //TODO: Use smell.getSmellyElements() to aggregate smelly classes and methods
            } finally {
                testFile.addDetectedSmell(null);
            }
        }

        return testFile;

    }

    private CompilationUnit parseIntoCompilationUnit(String testFilePath) throws FileNotFoundException {
        if (StringUtils.isEmpty(testFilePath)) {
            return null;
        }
        FileInputStream testFileInputStream;
        testFileInputStream = new FileInputStream(testFilePath);
        return JavaParser.parse(testFileInputStream);
    }


}
