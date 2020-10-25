package edu.rit.se.testsmells.testsmell.smell;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import edu.rit.se.testsmells.testsmell.AbstractSmell;
import edu.rit.se.testsmells.testsmell.TestMethod;

import java.io.FileNotFoundException;

public class SensitiveEquality extends AbstractSmell {


    private CompilationUnit testFileCompilationUnit;

    public SensitiveEquality() {
        super();
    }

    @Override
    public AbstractSmell recreate() {
        return new SensitiveEquality();
    }

    /**
     * Checks of 'Sensitive Equality' smell
     */
    @Override
    public String getSmellName() {
        return "Sensitive Equality";
    }

    /**
     * Analyze the test file for test methods the 'Sensitive Equality' smell
     */
    @Override
    public void runAnalysis(CompilationUnit testFileCompilationUnit, CompilationUnit productionFileCompilationUnit, String testFileName, String productionFileName) throws FileNotFoundException {
        SensitiveEquality.ClassVisitor classVisitor;
        classVisitor = new SensitiveEquality.ClassVisitor();
        this.testFileCompilationUnit = testFileCompilationUnit;
        classVisitor.visit(this.testFileCompilationUnit, null);
    }

    private class ClassVisitor extends VoidVisitorAdapter<Void> {
        private MethodDeclaration currentMethod = null;
        private int sensitiveCount = 0;
        TestMethod testMethod;

        // examine all methods in the test class
        @Override
        public void visit(MethodDeclaration n, Void arg) {
            if (isValidTestMethod(n)) {
                currentMethod = n;
                testMethod = new TestMethod(getFullMethodName(testFileCompilationUnit, n));
                testMethod.setHasSmell(false); //default value is false (i.e. no smell)
                super.visit(n, arg);

                testMethod.setHasSmell(sensitiveCount >= 1);
                testMethod.addDataItem("SensitiveCount", String.valueOf(sensitiveCount));

                addSmellyElement(testMethod);

                //reset values for next method
                currentMethod = null;
                sensitiveCount = 0;
            }
        }

        // examine the methods being called within the test method
        @Override
        public void visit(MethodCallExpr n, Void arg) {
            super.visit(n, arg);
            if (currentMethod != null) {
                // if the name of a method being called start with 'assert'
                if (n.getNameAsString().startsWith(("assert"))) {
                    // assert methods that contain toString
                    for (Expression argument : n.getArguments()) {
                        if (argument.toString().contains("toString")) {
                            sensitiveCount++;
                        }
                    }
                }
                // if the name of a method being called is 'fail'
                else if (n.getNameAsString().equals("fail")) {
                    // fail methods that contain toString
                    for (Expression argument : n.getArguments()) {
                        if (argument.toString().contains("toString")) {
                            sensitiveCount++;
                        }
                    }
                }

            }
        }

    }
}
