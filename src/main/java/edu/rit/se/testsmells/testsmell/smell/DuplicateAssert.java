package edu.rit.se.testsmells.testsmell.smell;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import edu.rit.se.testsmells.testsmell.AbstractSmell;
import edu.rit.se.testsmells.testsmell.TestMethod;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DuplicateAssert extends AbstractSmell {


    private CompilationUnit testFileCompilationUnit;

    public DuplicateAssert() {
        super();
    }

    @Override
    public AbstractSmell recreate() {
        return new DuplicateAssert();
    }

    /**
     * Checks of 'Duplicate Assert' smell
     */
    @Override
    public String getSmellName() {
        return "Duplicate Assert";
    }

    /**
     * Analyze the test file for test methods that have multiple assert statements with the same explanation message
     */
    @Override
    public void runAnalysis(CompilationUnit testFileCompilationUnit, CompilationUnit productionFileCompilationUnit, String testFileName, String productionFileName) throws FileNotFoundException {
        DuplicateAssert.ClassVisitor classVisitor;
        classVisitor = new DuplicateAssert.ClassVisitor();
        this.testFileCompilationUnit = testFileCompilationUnit;
        classVisitor.visit(this.testFileCompilationUnit, null);
    }


    private class ClassVisitor extends VoidVisitorAdapter<Void> {
        private MethodDeclaration currentMethod = null;
        TestMethod testMethod;
        List<String> assertMessage = new ArrayList<>();
        List<String> assertMethod = new ArrayList<>();

        // examine all methods in the test class
        @Override
        public void visit(MethodDeclaration n, Void arg) {
            if (isValidTestMethod(n)) {
                currentMethod = n;
                testMethod = new TestMethod(getFullMethodName(testFileCompilationUnit, n));
                testMethod.setHasSmell(false); //default value is false (i.e. no smell)
                super.visit(n, arg);

                // if there are duplicate messages, then the smell exists
                Set<String> set1 = new HashSet<>(assertMessage);
                if (set1.size() < assertMessage.size()) {
                    testMethod.setHasSmell(true);
                }

                // if there are duplicate assert methods, then the smell exists
                Set<String> set2 = new HashSet<>(assertMethod);
                if (set2.size() < assertMethod.size()) {
                    testMethod.setHasSmell(true);
                }

                addSmellyElement(testMethod);

                //reset values for next method
                currentMethod = null;
                assertMessage = new ArrayList<>();
                assertMethod = new ArrayList<>();
            }
        }

        // examine the methods being called within the test method
        @Override
        public void visit(MethodCallExpr n, Void arg) {
            super.visit(n, arg);
            if (currentMethod != null) {
                // if the name of a method being called start with 'assert'
                // if the name of a method being called is an assertion and has 3 parameters
                if (n.getNameAsString().startsWith(("assertArrayEquals")) ||
                        n.getNameAsString().startsWith(("assertEquals")) ||
                        n.getNameAsString().startsWith(("assertNotSame")) ||
                        n.getNameAsString().startsWith(("assertSame")) ||
                        n.getNameAsString().startsWith(("assertThat"))) {
                    assertMethod.add(n.toString());
                    // assert method contains a message
                    if (n.getArguments().size() == 3) {
                        assertMessage.add(n.getArgument(0).toString());
                    }

                }
                // if the name of a method being called is an assertion and has 2 parameters
                else if (n.getNameAsString().equals("assertFalse") ||
                        n.getNameAsString().equals("assertNotNull") ||
                        n.getNameAsString().equals("assertNull") ||
                        n.getNameAsString().equals("assertTrue")) {
                    assertMethod.add(n.toString());
                    // assert method contains a message
                    if (n.getArguments().size() == 2) {
                        assertMessage.add(n.getArgument(0).toString());
                    }
                }
                // if the name of a method being called is 'fail'
                else if (n.getNameAsString().equals("fail")) {
                    assertMethod.add(n.toString());
                    // fail method contains a message
                    if (n.getArguments().size() == 1) {
                        assertMessage.add(n.getArgument(0).toString());
                    }
                }

            }
        }

    }
}

