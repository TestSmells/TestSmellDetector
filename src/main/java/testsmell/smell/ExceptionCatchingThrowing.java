package testsmell.smell;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import testsmell.AbstractSmell;
import testsmell.SmellyElement;
import testsmell.TestMethod;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/*
This class checks if test methods in the class either catch or throw exceptions. Use Junit's exception handling to automatically pass/fail the test
If this code detects the existence of a catch block or a throw statement in the methods body, the method is marked as smelly
 */
public class ExceptionCatchingThrowing extends AbstractSmell {

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
    @Override
    public boolean getHasSmell() {
        return smellyElementList.stream().filter(x -> x.getHasSmell()).count() >= 1;
    }

    /**
     * Analyze the test file for test methods that have exception handling
     */
    @Override
    public void runAnalysis(String testFilePath, String productionFilePath) throws FileNotFoundException {
        FileInputStream testFileInputStream = null;
        try {
            testFileInputStream = new FileInputStream(testFilePath);
        } catch (FileNotFoundException e) {
            throw e;
        }

        CompilationUnit compilationUnit;
        ExceptionCatchingThrowing.ClassVisitor classVisitor;

        assert testFileInputStream != null;
        compilationUnit = JavaParser.parse(testFileInputStream);
        classVisitor = new ExceptionCatchingThrowing.ClassVisitor();
        classVisitor.visit(compilationUnit, null);
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
            //only analyze methods that either have a @test annotation (Junit 4) or the method name starts with 'test'
            if (n.getAnnotationByName("Test").isPresent() || n.getNameAsString().toLowerCase().startsWith("test")) {
                currentMethod = n;
                testMethod = new TestMethod(n.getNameAsString());
                testMethod.setHasSmell(false); //default value is false (i.e. no smell)
                super.visit(n, arg);

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
