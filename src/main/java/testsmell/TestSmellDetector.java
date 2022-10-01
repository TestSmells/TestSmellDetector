package testsmell;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.TypeDeclaration;
import org.apache.commons.lang3.StringUtils;
import testsmell.smell.*;
import thresholds.Thresholds;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TestSmellDetector {

    private List<AbstractSmell> testSmells;
    private Thresholds thresholds;

    /**
     * Instantiates the various test smell analyzer classes and loads the objects into an list.
     * Each smell analyzer is initialized with a threshold object to set the most appropriate rule for the detection
     *
     * @param thresholds it could be the default threshold of the ones defined by Spadini
     */
    public TestSmellDetector(Thresholds thresholds) {
        this.thresholds = thresholds;
        initializeSmells();
    }

    private void initializeSmells() {
        testSmells = new ArrayList<>();
        testSmells.add(new AssertionRoulette(thresholds));
        testSmells.add(new ConditionalTestLogic(thresholds));
        testSmells.add(new ConstructorInitialization(thresholds));
        testSmells.add(new DefaultTest(thresholds));
        testSmells.add(new EmptyTest(thresholds));
        testSmells.add(new ExceptionCatchingThrowing(thresholds));
        testSmells.add(new GeneralFixture(thresholds));
        testSmells.add(new MysteryGuest(thresholds));
        testSmells.add(new PrintStatement(thresholds));
        testSmells.add(new RedundantAssertion(thresholds));
        testSmells.add(new SensitiveEquality(thresholds));
        testSmells.add(new VerboseTest(thresholds));
        testSmells.add(new SleepyTest(thresholds));
        testSmells.add(new EagerTest(thresholds));
        testSmells.add(new LazyTest(thresholds));
        testSmells.add(new DuplicateAssert(thresholds));
        testSmells.add(new UnknownTest(thresholds));
        testSmells.add(new IgnoredTest(thresholds));
        testSmells.add(new ResourceOptimism(thresholds));
        testSmells.add(new MagicNumberTest(thresholds));
        testSmells.add(new DependentTest(thresholds));
    }

    public void setTestSmells(List<AbstractSmell> testSmells) {
        this.testSmells = testSmells;
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
     * Loads the java source code file into an AST and then analyzes it for the existence of the different types of
     * test smells
     */
    public TestFile detectSmells(TestFile testFile) throws IOException {
        initializeSmells();
        CompilationUnit testFileCompilationUnit = null;
        CompilationUnit productionFileCompilationUnit = null;
        FileInputStream testFileInputStream, productionFileInputStream;

        if (!StringUtils.isEmpty(testFile.getTestFilePath())) {
            testFileInputStream = new FileInputStream(testFile.getTestFilePath());
            testFileCompilationUnit = Util.parseJava(testFileInputStream);
            TypeDeclaration typeDeclaration = testFileCompilationUnit.getTypes().get(0);
            testFile.setNumberOfTestMethods(typeDeclaration.getMethods().size());
        }

        if (!StringUtils.isEmpty(testFile.getProductionFilePath())) {
            productionFileInputStream = new FileInputStream(testFile.getProductionFilePath());
            productionFileCompilationUnit = Util.parseJava(productionFileInputStream);
        }

        for (AbstractSmell smell : testSmells) {
            try {
                smell.runAnalysis(testFileCompilationUnit, productionFileCompilationUnit,
                        testFile.getTestFileNameWithoutExtension(),
                        testFile.getProductionFileNameWithoutExtension());
            } catch (FileNotFoundException e) {
                testFile.addSmell(null);
                continue;
            }
            testFile.addSmell(smell);
        }
        return testFile;
    }
}
