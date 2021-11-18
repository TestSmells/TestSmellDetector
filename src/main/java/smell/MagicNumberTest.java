package testsmell.smell;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import testsmell.AbstractSmell;
import testsmell.TestMethod;
import testsmell.Util;
import thresholds.Thresholds;

import java.io.FileNotFoundException;

public class MagicNumberTest extends AbstractSmell {

    public MagicNumberTest(Thresholds thresholds) {
        super(thresholds);
    }

    /**
     * Checks of 'MagicNumberTest' smell
     */
    @Override
    public String getSmellName() {
        return "Magic Number Test";
    }

    /**
     * Analyze the test file for test methods that have magic numbers in as parameters in the assert methods
     */
    @Override
    public void runAnalysis(CompilationUnit testFileCompilationUnit, CompilationUnit productionFileCompilationUnit, String testFileName, String productionFileName) throws FileNotFoundException {
        MagicNumberTest.ClassVisitor classVisitor;
        classVisitor = new MagicNumberTest.ClassVisitor();
        classVisitor.visit(testFileCompilationUnit, null);
    }

    private class ClassVisitor extends VoidVisitorAdapter<Void> {
        private MethodDeclaration currentMethod = null;
        private MagicNumberTest magicNumberTest;
        TestMethod testMethod;
        private int magicCount = 0;

        // examine all methods in the test class
        @Override
        public void visit(MethodDeclaration n, Void arg) {
            if (Util.isValidTestMethod(n)) {
                currentMethod = n;
                testMethod = new TestMethod(n.getNameAsString());
                testMethod.setSmell(false); //default value is false (i.e. no smell)
                super.visit(n, arg);

                testMethod.setSmell(magicCount >= thresholds.getMagicNumberTest());
                testMethod.addDataItem("MagicNumberCount", String.valueOf(magicCount));
                smellyElementsSet.add(testMethod);

                //reset values for next method
                currentMethod = null;
                magicCount = 0;
            }
        }

        // examine the methods being called within the test method
        @Override
        public void visit(MethodCallExpr n, Void arg) {
            super.visit(n, arg);
            if (currentMethod != null) {
                // if the name of a method being called start with 'assert'
                if (n.getNameAsString().startsWith(("assertArrayEquals")) ||
                        n.getNameAsString().startsWith(("assertEquals")) ||
                        n.getNameAsString().startsWith(("assertNotSame")) ||
                        n.getNameAsString().startsWith(("assertSame")) ||
                        n.getNameAsString().startsWith(("assertThat")) ||
                        n.getNameAsString().equals("assertNotNull") ||
                        n.getNameAsString().equals("assertNull")) {
                    // checks all arguments of the assert method
                    for (Expression argument : n.getArguments()) {
                        // if the argument is a number
                        if (Util.isNumber(argument.toString())) {
                            magicCount++;
                        }
                        // if the argument contains an ObjectCreationExpr (e.g. assertEquals(new Integer(2),...)
                        else if (argument instanceof ObjectCreationExpr) {
                            for (Expression objectArguments : ((ObjectCreationExpr) argument).getArguments()) {
                                if (Util.isNumber(objectArguments.toString())) {
                                    magicCount++;
                                }
                            }
                        }
                        // if the argument contains an MethodCallExpr (e.g. assertEquals(someMethod(2),...)
                        else if (argument instanceof MethodCallExpr) {
                            for (Expression objectArguments : ((MethodCallExpr) argument).getArguments()) {
                                if (Util.isNumber(objectArguments.toString())) {
                                    magicCount++;
                                }
                            }
                        }
                    }
                }
            }
        }

    }

}
