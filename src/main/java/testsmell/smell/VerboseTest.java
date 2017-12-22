package testsmell.smell;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import testsmell.AbstractSmell;
import testsmell.SmellyElement;
import testsmell.TestMethod;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/*
If a test methods contains a statements that exceeds a certain threshold, the method is marked as smelly
 */
public class VerboseTest extends AbstractSmell {

    private List<SmellyElement> smellyElementList;

    public VerboseTest() {
        smellyElementList = new ArrayList<>();
    }

    /**
     * Checks of 'Verbose Test' smell
     */
    @Override
    public String getSmellName() {
        return "Verbose Test";
    }

    /**
     * Returns true if any of the elements has a smell
     */
    @Override
    public boolean getHasSmell() {
        return smellyElementList.stream().filter(x -> x.getHasSmell()).count() >= 1;
    }

    /**
     * Analyze the test file for test methods for the 'Verbose Test' smell
     */
    @Override
    public void runAnalysis(CompilationUnit testFileCompilationUnit,CompilationUnit productionFileCompilationUnit, String testFileName, String productionFileName) throws FileNotFoundException {
        VerboseTest.ClassVisitor classVisitor;
        classVisitor = new VerboseTest.ClassVisitor();
        classVisitor.visit(testFileCompilationUnit, null);
    }

    /**
     * Returns the set of analyzed elements (i.e. test methods)
     */
    @Override
    public List<SmellyElement> getSmellyElements() {
        return smellyElementList;
    }

    private class ClassVisitor extends VoidVisitorAdapter<Void> {
        final int MAX_STATEMENTS = 123;
        private MethodDeclaration currentMethod = null;
        private int verboseCount = 0;
        TestMethod testMethod;

        // examine all methods in the test class
        @Override
        public void visit(MethodDeclaration n, Void arg) {
            if (!n.getAnnotationByName("Ignore").isPresent()) {
                //only analyze methods that either have a @test annotation (Junit 4) or the method name starts with 'test'
                if (n.getAnnotationByName("Test").isPresent() || n.getNameAsString().toLowerCase().startsWith("test")) {
                    currentMethod = n;
                    testMethod = new TestMethod(n.getNameAsString());
                    testMethod.setHasSmell(false); //default value is false (i.e. no smell)

                    //method should not be abstract
                    if (!currentMethod.isAbstract()) {
                        if (currentMethod.getBody().isPresent()) {
                            //get the total number of statements contained in the method
                            if (currentMethod.getBody().get().getStatements().size() >= MAX_STATEMENTS) {
                                verboseCount++;
                            }
                        }
                    }
                    testMethod.setHasSmell(verboseCount >= 1);
                    testMethod.addDataItem("VerboseCount", String.valueOf(verboseCount));

                    smellyElementList.add(testMethod);

                    //reset values for next method
                    currentMethod = null;
                    verboseCount = 0;
                }
            }
        }
    }
}
