package testsmell.smell;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import testsmell.AbstractSmell;
import testsmell.SmellyElement;
import testsmell.TestMethod;
import testsmell.Util;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * "Guess what's wrong?" This smell comes from having a number of assertions in a test method that have no explanation.
 * If one of the assertions fails, you do not know which one it is.
 * A. van Deursen, L. Moonen, A. Bergh, G. Kok, “Refactoring Test Code”, Technical Report, CWI, 2001.
 */
public class AssertionRoulette extends AbstractSmell {

    /**
     * Checks of 'Assertion Roulette' smell
     */
    @Override
    public String getSmellName() {
        return "Assertion Roulette";
    }

    /**
     * Analyze the test file for test methods for multiple assert statements without an explanation/message
     */
    @Override
    public void runAnalysis(CompilationUnit testFileCompilationUnit, CompilationUnit productionFileCompilationUnit, String testFileName, String productionFileName) throws FileNotFoundException {
        AssertionRoulette.ClassVisitor classVisitor;
        classVisitor = new AssertionRoulette.ClassVisitor();
        classVisitor.visit(testFileCompilationUnit, null);
    }

    private class ClassVisitor extends VoidVisitorAdapter<Void> {
        private MethodDeclaration currentMethod = null;
        private int assertNoMessageCount = 0;
        private int assertCount = 0;
        TestMethod testMethod;

        // examine all methods in the test class
        @Override
        public void visit(MethodDeclaration n, Void arg) {
            if (Util.isValidTestMethod(n)) {
                currentMethod = n;
                testMethod = new TestMethod(n.getNameAsString());
                testMethod.setHasSmell(false); //default value is false (i.e. no smell)
                super.visit(n, arg);


                // if there is only 1 assert statement in the method, then a explanation message is not needed
                if (assertCount == 1)
                    testMethod.setHasSmell(false);
                else if (assertNoMessageCount >= 1) //if there is more than one assert statement, then all the asserts need to have an explanation message
                    testMethod.setHasSmell(true);

                testMethod.addDataItem("AssertCount", String.valueOf(assertNoMessageCount));

                smellyElementList.add(testMethod);

                //reset values for next method
                currentMethod = null;
                assertCount = 0;
                assertNoMessageCount = 0;
            }
        }

        // examine the methods being called within the test method
        @Override
        public void visit(MethodCallExpr n, Void arg) {
            super.visit(n, arg);
            if (currentMethod != null) {
                // if the name of a method being called is an assertion and has 3 parameters
                if (n.getNameAsString().startsWith(("assertArrayEquals")) ||
                        n.getNameAsString().startsWith(("assertEquals")) ||
                        n.getNameAsString().startsWith(("assertNotSame")) ||
                        n.getNameAsString().startsWith(("assertSame")) ||
                        n.getNameAsString().startsWith(("assertThat"))) {
                    assertCount++;
                    // assert methods that do not contain a message
                    if (n.getArguments().size() < 3) {
                        assertNoMessageCount++;
                    }
                }
                // if the name of a method being called is an assertion and has 2 parameters
                else if (n.getNameAsString().equals("assertFalse") ||
                        n.getNameAsString().equals("assertNotNull") ||
                        n.getNameAsString().equals("assertNull") ||
                        n.getNameAsString().equals("assertTrue")) {
                    assertCount++;
                    // assert methods that do not contain a message
                    if (n.getArguments().size() < 2) {
                        assertNoMessageCount++;
                    }
                }

                // if the name of a method being called is 'fail'
                else if (n.getNameAsString().equals("fail")) {
                    assertCount++;
                    // fail method does not contain a message
                    if (n.getArguments().size() < 1) {
                        assertNoMessageCount++;
                    }
                }

            }
        }

    }
}

