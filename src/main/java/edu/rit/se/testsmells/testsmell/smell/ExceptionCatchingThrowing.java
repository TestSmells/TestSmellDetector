package edu.rit.se.testsmells.testsmell.smell;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import edu.rit.se.testsmells.testsmell.AbstractSmell;
import edu.rit.se.testsmells.testsmell.SmellyElement;
import edu.rit.se.testsmells.testsmell.TestMethod;
import edu.rit.se.testsmells.testsmell.Util;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/*
This class checks if test methods in the class either catch or throw exceptions. Use Junit's exception handling to automatically pass/fail the test
If this code detects the existence of a catch block or a throw statement in the methods body, the method is marked as smelly
 */
public class ExceptionCatchingThrowing implements AbstractSmell {

    private List<SmellyElement> smellyElementList;

    public ExceptionCatchingThrowing() {
        smellyElementList = new ArrayList<>();
    }

    /**
     * Checks of 'Exception Catching Throwing' smell
     */
    @Override
    public String getSmellName() {
        return "Exception Catching Throwing";
    }

    /**
     * Returns true if any of the elements has a smell
     */
    public boolean hasSmell() {
        return smellyElementList.stream().filter(x -> x.hasSmell()).count() >= 1;
    }

    /**
     * Analyze the test file for test methods that have exception handling
     */
    @Override
    public void runAnalysis(CompilationUnit testFileCompilationUnit, CompilationUnit productionFileCompilationUnit, String testFileName, String productionFileName) throws FileNotFoundException {
        ExceptionCatchingThrowing.ClassVisitor classVisitor;
        classVisitor = new ExceptionCatchingThrowing.ClassVisitor();
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
        private MethodDeclaration currentMethod = null;
        private int exceptionCount = 0;
        TestMethod testMethod;


        // examine all methods in the test class
        @Override
        public void visit(MethodDeclaration n, Void arg) {
            if (Util.isValidTestMethod(n)) {
                currentMethod = n;
                testMethod = new TestMethod(n.getNameAsString());
                testMethod.setHasSmell(false); //default value is false (i.e. no smell)
                super.visit(n, arg);

                if (n.getThrownExceptions().size() >= 1)
                    exceptionCount++;

                testMethod.setHasSmell(exceptionCount >= 1);
                testMethod.addDataItem("ExceptionCount", String.valueOf(exceptionCount));

                smellyElementList.add(testMethod);

                //reset values for next method
                currentMethod = null;
                exceptionCount = 0;
            }
        }


        @Override
        public void visit(ThrowStmt n, Void arg) {
            super.visit(n, arg);

            if (currentMethod != null) {
                exceptionCount++;
            }
        }

        @Override
        public void visit(CatchClause n, Void arg) {
            super.visit(n, arg);

            if (currentMethod != null) {
                exceptionCount++;
            }
        }

    }
}
