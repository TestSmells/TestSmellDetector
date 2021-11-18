package testsmell.smell;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import testsmell.AbstractSmell;
import testsmell.SmellyElement;
import testsmell.TestMethod;
import testsmell.Util;
import thresholds.Thresholds;

import java.io.FileNotFoundException;
import java.util.List;

/*
If a test methods contains a statements that exceeds a certain threshold, the method is marked as smelly
 */
public class VerboseTest extends AbstractSmell {

    public VerboseTest(Thresholds thresholds) {
        super(thresholds);
    }

    /**
     * Checks of 'Verbose Test' smell
     */
    @Override
    public String getSmellName() {
        return "Verbose Test";
    }

    /**
     * Analyze the test file for test methods for the 'Verbose Test' smell
     */
    @Override
    public void runAnalysis(CompilationUnit testFileCompilationUnit, CompilationUnit productionFileCompilationUnit, String testFileName, String productionFileName) throws FileNotFoundException {
        VerboseTest.ClassVisitor classVisitor;
        classVisitor = new VerboseTest.ClassVisitor();
        classVisitor.visit(testFileCompilationUnit, null);
    }

    private class ClassVisitor extends VoidVisitorAdapter<Void> {
        final int MAX_STATEMENTS = 123;
        private MethodDeclaration currentMethod = null;
        private int verboseCount = 0;
        TestMethod testMethod;

        // examine all methods in the test class
        @Override
        public void visit(MethodDeclaration n, Void arg) {
            if (Util.isValidTestMethod(n)) {
                currentMethod = n;
                testMethod = new TestMethod(n.getNameAsString());
                testMethod.setSmell(false); //default value is false (i.e. no smell)

                //method should not be abstract
                if (!currentMethod.isAbstract()) {
                    if (currentMethod.getBody().isPresent()) {
                        //get the total number of statements contained in the method
                        if (currentMethod.getBody().get().getStatements().size() >= MAX_STATEMENTS) {
                            verboseCount++;
                        }
                    }
                }
                boolean isSmelly = verboseCount > thresholds.getVerboseTest();
                testMethod.setSmell(isSmelly);
                testMethod.addDataItem("VerboseCount", String.valueOf(verboseCount));

                smellyElementsSet.add(testMethod);

                //reset values for next method
                currentMethod = null;
                verboseCount = 0;
            }
        }
    }
}
