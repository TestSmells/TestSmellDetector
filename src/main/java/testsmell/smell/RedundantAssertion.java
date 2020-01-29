package testsmell.smell;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import testsmell.AbstractSmell;
import testsmell.SmellyElement;
import testsmell.TestMethod;
import testsmell.Util;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/*
If a test method contains an assert statement that explicitly returns a true or false, the method is marked as smelly
 */
public class RedundantAssertion extends AbstractSmell {

    /**
     * Checks of 'Redundant Assertion' smell
     */
    @Override
    public String getSmellName() {
        return "Redundant Assertion";
    }

    /**
     * Analyze the test file for test methods for multiple assert statements
     */
    @Override
    public void runAnalysis(CompilationUnit testFileCompilationUnit, CompilationUnit productionFileCompilationUnit, String testFileName, String productionFileName) throws FileNotFoundException {
        RedundantAssertion.ClassVisitor classVisitor;
        classVisitor = new RedundantAssertion.ClassVisitor();
        classVisitor.visit(testFileCompilationUnit, null);
    }

    private class ClassVisitor extends VoidVisitorAdapter<Void> {
        private MethodDeclaration currentMethod = null;
        private int redundantCount = 0;
        TestMethod testMethod;

        // examine all methods in the test class
        @Override
        public void visit(MethodDeclaration n, Void arg) {
            if (Util.isValidTestMethod(n)) {
                currentMethod = n;
                testMethod = new TestMethod(n.getNameAsString());
                testMethod.setHasSmell(false); //default value is false (i.e. no smell)
                super.visit(n, arg);

                testMethod.setHasSmell(redundantCount >= 1);
                testMethod.addDataItem("RedundantCount", String.valueOf(redundantCount));

                smellyElementList.add(testMethod);

                //reset values for next method
                currentMethod = null;
                redundantCount = 0;
            }
        }


        @Override
        public void visit(MethodCallExpr n, Void arg) {
            String argumentValue = null;

            super.visit(n, arg);
            if (currentMethod != null) {
                switch (n.getNameAsString()) {
                    case "assertTrue":
                    case "assertFalse":
                        if (n.getArguments().size() == 1 && n.getArgument(0) instanceof BooleanLiteralExpr) { // assertTrue(boolean condition) or assertFalse(boolean condition)
                            argumentValue = Boolean.toString(((BooleanLiteralExpr) n.getArgument(0)).getValue());
                        } else if (n.getArguments().size() == 2 && n.getArgument(1) instanceof BooleanLiteralExpr) { // assertTrue(java.lang.String message, boolean condition)  or assertFalse(java.lang.String message, boolean condition)
                            argumentValue = Boolean.toString(((BooleanLiteralExpr) n.getArgument(1)).getValue());
                        }

                        if (argumentValue != null && (argumentValue.toLowerCase().equals("true") || argumentValue.toLowerCase().equals("false"))) {
                            redundantCount++;
                        }
                        break;

                    case "assertNotNull":
                    case "assertNull":
                        if (n.getArguments().size() == 1 && n.getArgument(0) instanceof NullLiteralExpr) { // assertNotNull(java.lang.Object object) or assertNull(java.lang.Object object)
                            argumentValue = (((NullLiteralExpr) n.getArgument(0)).toString());
                        } else if (n.getArguments().size() == 2 && n.getArgument(1) instanceof NullLiteralExpr) { // assertNotNull(java.lang.String message, java.lang.Object object) or assertNull(java.lang.String message, java.lang.Object object)
                            argumentValue = (((NullLiteralExpr) n.getArgument(1)).toString());
                        }

                        if (argumentValue != null && (argumentValue.toLowerCase().equals("null"))) {
                            redundantCount++;
                        }
                        break;

                    default:
                        if (n.getNameAsString().startsWith("assert")) {
                            if (n.getArguments().size() == 2) { //e.g. assertArrayEquals(byte[] expecteds, byte[] actuals); assertEquals(long expected, long actual);
                                if (n.getArgument(0).equals(n.getArgument(1))) {
                                    redundantCount++;
                                }
                            }
                            if (n.getArguments().size() == 3) { //e.g. assertArrayEquals(java.lang.String message, byte[] expecteds, byte[] actuals); assertEquals(java.lang.String message, long expected, long actual)
                                if (n.getArgument(1).equals(n.getArgument(2))) {
                                    redundantCount++;
                                }
                            }
                        }
                        break;
                }
            }
        }

    }
}
