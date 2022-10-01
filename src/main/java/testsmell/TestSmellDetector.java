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

    private List<SmellFactory> testSmells;
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
        testSmells.add(AssertionRoulette::new);
        testSmells.add(ConditionalTestLogic::new);
        testSmells.add(ConstructorInitialization::new);
        testSmells.add(DefaultTest::new);
        testSmells.add(EmptyTest::new);
        testSmells.add(ExceptionCatchingThrowing::new);
        testSmells.add(GeneralFixture::new);
        testSmells.add(MysteryGuest::new);
        testSmells.add(PrintStatement::new);
        testSmells.add(RedundantAssertion::new);
        testSmells.add(SensitiveEquality::new);
        testSmells.add(VerboseTest::new);
        testSmells.add(SleepyTest::new);
        testSmells.add(EagerTest::new);
        testSmells.add(LazyTest::new);
        testSmells.add(DuplicateAssert::new);
        testSmells.add(UnknownTest::new);
        testSmells.add(IgnoredTest::new);
        testSmells.add(ResourceOptimism::new);
        testSmells.add(MagicNumberTest::new);
        testSmells.add(DependentTest::new);
    }

    public void setTestSmells(List<SmellFactory> testSmells) {
        this.testSmells = testSmells;
    }

    /**
     * Provides the names of the smells that are being checked for in the code
     *
     * @return list of smell names
     */
    public List<String> getTestSmellNames() {
        return testSmells.stream()
            .map(factory -> factory.createInstance(thresholds))
            .map(AbstractSmell::getSmellName)
            .collect(Collectors.toList());
    }

    /**
     * Loads the java source code file into an AST and then analyzes it for the existence of the different types of
     * test smells
     */
    public TestFile detectSmells(TestFile testFile) throws IOException {
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

        for (SmellFactory smellFactory : testSmells) {
            AbstractSmell smell = smellFactory.createInstance(thresholds);
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
