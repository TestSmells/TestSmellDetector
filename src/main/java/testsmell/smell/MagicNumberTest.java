package testsmell.smell;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import testsmell.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class MagicNumberTest  extends AbstractSmell {

    private List<SmellyElement> smellyElementList;

    public MagicNumberTest() {
        smellyElementList = new ArrayList<>();
    }

    /**
     * Checks of 'MagicNumberTest' smell
     */
    @Override
    public String getSmellName() {
        return "Magic Number Test";
    }

    /**
     * Returns true if any of the elements has a smell
     */
    @Override
    public boolean getHasSmell() {
        return smellyElementList.stream().filter(x -> x.getHasSmell()).count() >= 1;
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

    /**
     * Returns the set of analyzed elements (i.e. test methods)
     */
    @Override
    public List<SmellyElement> getSmellyElements() {
        return smellyElementList;
    }

    private class ClassVisitor extends VoidVisitorAdapter<Void> {
        private MethodDeclaration currentMethod = null;
        TestMethod testMethod;
        private int magicCount = 0;

        // examine all methods in the test class
        @Override
        public void visit(MethodDeclaration n, Void arg) {
            if (Util.isValidTestMethod(n)) {
                currentMethod = n;
                testMethod = new TestMethod(n.getNameAsString());
                testMethod.setHasSmell(false); //default value is false (i.e. no smell)
                super.visit(n, arg);

                testMethod.setHasSmell(magicCount >= DetectionThresholds.MAGIC_NUMBER_TEST);
                testMethod.addDataItem("MagicNumberCount", String.valueOf(magicCount));

                smellyElementList.add(testMethod);

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
                        if(Util.isNumber(argument.toString())){
                           magicCount++;
                       }
                       // if the argument contains an ObjectCreationExpr (e.g. assertEquals(new Integer(2),...)
                       else if(argument instanceof ObjectCreationExpr){
                           for (Expression objectArguments:((ObjectCreationExpr) argument).getArguments()){
                               if(Util.isNumber(objectArguments.toString())){
                                   magicCount++;
                               }
                           }
                       }
                       // if the argument contains an MethodCallExpr (e.g. assertEquals(someMethod(2),...)
                       else if(argument instanceof MethodCallExpr){
                           for (Expression objectArguments:((MethodCallExpr) argument).getArguments()){
                               if(Util.isNumber(objectArguments.toString())){
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
