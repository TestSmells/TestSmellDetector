package edu.rit.se.testsmells.testsmell.smell;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import edu.rit.se.testsmells.testsmell.AbstractSmell;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class DependentTest extends AbstractSmell {


    private List<TestMethod> testMethods;


    public DependentTest() {
        super();
        testMethods = new ArrayList<>();
    }

    /**
     * Checks of 'DependentTest' smell
     */
    @Override
    public String getSmellName() {
        return "Dependent Test";
    }

    /**
     * Analyze the test file for test methods that call other test methods
     */
    @Override
    public void runAnalysis(CompilationUnit testFileCompilationUnit, CompilationUnit productionFileCompilationUnit, String testFileName, String productionFileName) throws FileNotFoundException {
        DependentTest.ClassVisitor classVisitor;
        classVisitor = new DependentTest.ClassVisitor();
        classVisitor.visit(testFileCompilationUnit, null);

        for (TestMethod testMethod : testMethods) {
            if (testMethod.getCalledMethods().stream().anyMatch(x -> x.getName().equals(testMethods.stream().map(z -> z.getMethodDeclaration().getNameAsString())))){
                addSmellyElement(new edu.rit.se.testsmells.testsmell.TestMethod(getFullMethodName(testFileCompilationUnit, testMethod.getMethodDeclaration())));
            }
        }
    }

    private class ClassVisitor extends VoidVisitorAdapter<Void> {
        private MethodDeclaration currentMethod = null;
        List<CalledMethod> calledMethods;

        // examine all methods in the test class
        @Override
        public void visit(MethodDeclaration n, Void arg) {
            if (isValidTestMethod(n)) {
                currentMethod = n;
                calledMethods = new ArrayList<>();

                super.visit(n, arg);
                testMethods.add(new DependentTest.TestMethod(n, calledMethods));
            }
        }

        // examine the methods being called within the test method
        @Override
        public void visit(MethodCallExpr n, Void arg) {
            super.visit(n, arg);
            if (currentMethod != null) {
                if (!calledMethods.contains(new CalledMethod(n.getArguments().size(), n.getNameAsString()))) {
                    calledMethods.add(new CalledMethod(n.getArguments().size(), n.getNameAsString()));
                }
            }
        }
    }

    private class TestMethod {
        private List<CalledMethod> calledMethods;
        private MethodDeclaration methodDeclaration;

        public TestMethod(MethodDeclaration methodDeclaration, List<CalledMethod> calledMethods) {
            this.methodDeclaration = methodDeclaration;
            this.calledMethods = calledMethods;
        }

        public List<CalledMethod> getCalledMethods() {
            return calledMethods;
        }

        public MethodDeclaration getMethodDeclaration() {
            return methodDeclaration;
        }
    }

    private class CalledMethod {
        private int totalArguments;
        private String name;

        public CalledMethod(int totalArguments, String name) {
            this.totalArguments = totalArguments;
            this.name = name;
        }

        public int getTotalArguments() {
            return totalArguments;
        }

        public String getName() {
            return name;
        }
    }
}
