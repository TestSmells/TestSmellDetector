package edu.rit.se.testsmells.testsmell.smell;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import edu.rit.se.testsmells.testsmell.AbstractSmell;
import edu.rit.se.testsmells.testsmell.TestMethod;

import java.io.FileNotFoundException;

/**
 * This class checks if a test method is empty (i.e. the method does not contain statements in its body)
 * If the the number of statements in the body is 0, then the method is smelly
 */
public class EmptyTest extends AbstractSmell {


    private CompilationUnit testFileCompilationUnit;

    public EmptyTest() {
        super();
    }

    /**
     * Checks of 'Empty Test' smell
     */
    @Override
    public String getSmellName() {
        return "EmptyTest";
    }

    /**
     * Analyze the test file for test methods that are empty (i.e. no method body)
     */
    @Override
    public void runAnalysis(CompilationUnit testFileCompilationUnit, CompilationUnit productionFileCompilationUnit, String testFileName, String productionFileName) throws FileNotFoundException {
        EmptyTest.ClassVisitor classVisitor;
        classVisitor = new EmptyTest.ClassVisitor();
        this.testFileCompilationUnit = testFileCompilationUnit;
        classVisitor.visit(this.testFileCompilationUnit, null);
    }

    /**
     * Visitor class
     */
    private class ClassVisitor extends VoidVisitorAdapter<Void> {
        TestMethod testMethod;

        /**
         * The purpose of this method is to 'visit' all test methods in the test file
         */
        @Override
        public void visit(MethodDeclaration n, Void arg) {
            if (isValidTestMethod(n)) {
                testMethod = new TestMethod(getFullMethodName(testFileCompilationUnit, n));
                testMethod.setHasSmell(false); //default value is false (i.e. no smell)
                //method should not be abstract
                if (!n.isAbstract()) {
                    if (n.getBody().isPresent()) {
                        //get the total number of statements contained in the method
                        if (n.getBody().get().getStatements().size() == 0) {
                            testMethod.setHasSmell(true); //the method has no statements (i.e no body)
                        }
                    }
                }
                addSmellyElement(testMethod);
            }
        }
    }
}
