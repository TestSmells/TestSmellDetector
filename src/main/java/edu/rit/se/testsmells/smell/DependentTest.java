package edu.rit.se.testsmells.smell;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import edu.rit.se.testsmells.AbstractSmell;
import edu.rit.se.testsmells.Util;

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
     * Returns true if any of the elements has a smell
     */
    @Override
    public boolean getHasSmell() {
        return smellyElementList.stream().filter(x -> x.getHasSmell()).count() >= 1;
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
                smellyElementList.add(new edu.rit.se.testsmells.TestMethod(testMethod.getMethodDeclaration().getNameAsString()));
            }
        }

/*
        for (int i = 0; i < testMethods.get(i).getCalledMethods().size(); i++) {
            for (TestMethod testMethod : testMethods) {
                if (testMethods.get(i).getCalledMethods().stream().anyMatch(x -> x.getName().equals(testMethod.getMethodDeclaration().getNameAsString()))) {
                    smellyElementList.add(new testsmell.TestMethod(testMethod.getMethodDeclaration().getNameAsString()));
                }
            }
        }*/
    }

    private class ClassVisitor extends VoidVisitorAdapter<Void> {
        private MethodDeclaration currentMethod = null;
        List<CalledMethod> calledMethods;

        // examine all methods in the test class
        @Override
        public void visit(MethodDeclaration n, Void arg) {
            if (Util.isValidTestMethod(n)) {
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
        public List<CalledMethod> getCalledMethods() {
            return calledMethods;
        }

        public MethodDeclaration getMethodDeclaration() {
            return methodDeclaration;
        }

        public TestMethod(MethodDeclaration methodDeclaration, List<CalledMethod> calledMethods) {
            this.methodDeclaration = methodDeclaration;
            this.calledMethods = calledMethods;
        }

        private List<CalledMethod> calledMethods;
        private MethodDeclaration methodDeclaration;
    }

    private class CalledMethod {
        public int getTotalArguments() {
            return totalArguments;
        }

        public String getName() {
            return name;
        }

        public CalledMethod(int totalArguments, String name) {
            this.totalArguments = totalArguments;
            this.name = name;
        }

        private int totalArguments;
        private String name;
    }
}
