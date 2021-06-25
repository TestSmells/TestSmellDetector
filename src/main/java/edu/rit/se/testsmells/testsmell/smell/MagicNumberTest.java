package edu.rit.se.testsmells.testsmell.smell;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import edu.rit.se.testsmells.testsmell.AbstractSmell;
import edu.rit.se.testsmells.testsmell.TestMethod;

import java.io.FileNotFoundException;

public class MagicNumberTest extends AbstractSmell {


    private CompilationUnit testFileCompilationUnit;

    public MagicNumberTest() {
        super();
    }

    @Override
    public AbstractSmell recreate() {
        return new MagicNumberTest();
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
        this.testFileCompilationUnit = testFileCompilationUnit;
        classVisitor.visit(this.testFileCompilationUnit, null);
    }

    private class ClassVisitor extends VoidVisitorAdapter<Void> {
        private MethodDeclaration currentMethod = null;
        TestMethod testMethod;
        private int magicCount = 0;

        // examine all methods in the test class
        @Override
        public void visit(MethodDeclaration n, Void arg) {
            if (isValidTestMethod(n)) {
                currentMethod = n;
                testMethod = new TestMethod(getFullMethodName(testFileCompilationUnit, n));
                testMethod.setHasSmell(false); //default value is false (i.e. no smell)
                super.visit(n, arg);

                testMethod.setHasSmell(magicCount >= 1);
                testMethod.addDataItem("MagicNumberCount", String.valueOf(magicCount));

                addSmellyElement(testMethod);

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
                    for (Expression argument:n.getArguments()) {
                        // if the argument is a number
                        if (isNumber(argument.toString())) {
                            magicCount++;
                        }
                        // if the argument contains an ObjectCreationExpr (e.g. assertEquals(new Integer(2),...)
                        else if (argument instanceof ObjectCreationExpr) {
                            for (Expression objectArguments : ((ObjectCreationExpr) argument).getArguments()) {
                                if (isNumber(objectArguments.toString())) {
                                    magicCount++;
                                }
                            }
                        }
                       // if the argument contains an MethodCallExpr (e.g. assertEquals(someMethod(2),...)
                       else if(argument instanceof MethodCallExpr){
                           for (Expression objectArguments:((MethodCallExpr) argument).getArguments()){
                               if (isNumber(objectArguments.toString())) {
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
