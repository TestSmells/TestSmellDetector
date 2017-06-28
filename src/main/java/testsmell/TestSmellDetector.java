package testsmell;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import testsmell.smell.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TestSmellDetector {

    private List<ITestSmell> testSmells;

    /**
     * Instantiates the various test smell analyzer classes and loads the objects into an List
     */
    private TestSmellDetector() {

        testSmells = new ArrayList<>();
        testSmells.add(new AssertionRoulette());
        testSmells.add(new ConditionalTestLogic());
        testSmells.add(new ConstructorInitialization());
        testSmells.add(new DefaultTest());
        testSmells.add(new EmptyTest());
        testSmells.add(new ExceptionCatchingThrowing());
        testSmells.add(new GeneralFixture());
        testSmells.add(new MysteryGuest());
        testSmells.add(new PrintStatement());
        testSmells.add(new RedundantAssertion());
        testSmells.add(new SensitiveEquality());
        testSmells.add(new VerboseTest());
        testSmells.add(new WaitAndSee());
    }

    /**
     * Factory method that provides a new instance of the TestSmellDetector
     * @return new TestSmellDetector instance
     */
    public static TestSmellDetector createTestSmellDetector() {
        return new TestSmellDetector();
    }

    /**
     * Provides the names of the smells that are being checked for in the code
     * @return list of smell names
     */
    public List<String> getTestSmellName() {
        return testSmells.stream().map(ITestSmell::getSmellNameAsString).collect(Collectors.toList());
    }

    /**
     * Loads the java source code file into an AST and then analyzes it for the existence of the different types of test smells
     * @param absoluteFilePath  the name of the source code that is to be analyzed for test smells
     * @return  a dictionary indicating if a smell occurs or not
     * @throws IOException
     */
    public Map<String, String> detectSmells(String absoluteFilePath) throws IOException {
        Map<String, String> analysisResult = null;

        if (absoluteFilePath.length() != 0) {
            FileInputStream fileInputStream = new FileInputStream(absoluteFilePath);
            CompilationUnit compilationUnit = JavaParser.parse(fileInputStream);

            analysisResult = new HashMap<>();
            analysisResult.put("FilePath", absoluteFilePath);

            for (ITestSmell smell : testSmells) {
                if (smell.runAnalysis(compilationUnit).stream().filter(ISmell::isHasSmell).collect(Collectors.toList()).size() >= 1) {
                    analysisResult.put(smell.getSmellNameAsString(), "true");
                } else {
                    analysisResult.put(smell.getSmellNameAsString(), "false");
                }
            }
        }

        return analysisResult;

    }


}
