package edu.rit.se.testsmells.testsmell;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import org.apache.commons.lang3.StringUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TestSmellDetector {

    private List<InputStream> inputStreams;
    private List<AbstractSmell> testSmells;

    /**
     * Instantiates the various test smell analyzer classes and loads the objects into an List
     */
    public TestSmellDetector() {
        testSmells = new ArrayList<>();
        inputStreams = new ArrayList<>();
    }

    public void addDetectableSmell(AbstractSmell smell) {
        testSmells.add(smell);
    }

    public void clear() {
        for (InputStream inputStream : inputStreams) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (AbstractSmell smell : testSmells) {
            smell.clear();
        }
        testSmells.clear();
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
    public void detectSmells(TestFile testFile) {
        CompilationUnit testFileCompilationUnit = parseIntoCompilationUnit(testFile.getTestFilePath());

        CompilationUnit productionFileCompilationUnit = parseIntoCompilationUnit(testFile.getProductionFilePath());

        for (AbstractSmell smellPrototype : testSmells) {
            AbstractSmell smell = smellPrototype.recreate();
            try {
                smell.runAnalysis(testFileCompilationUnit, productionFileCompilationUnit, testFile.getTestFileNameWithoutExtension(), testFile.getProductionFileNameWithoutExtension());
            } catch (FileNotFoundException ignored) {
            }

            testFile.addDetectedSmell(smell);
        }

    }

    private CompilationUnit parseIntoCompilationUnit(String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            return null;
        }
        InputStream testFileInputStream = null;
        try {
            testFileInputStream = new FileInputStream(filePath);
        } catch (IOException e) {
            testFileInputStream = getClass().getResourceAsStream(filePath);
        } finally {
            inputStreams.add(testFileInputStream);
        }
        assert Objects.nonNull(testFileInputStream);
        return JavaParser.parse(testFileInputStream);
    }
}
