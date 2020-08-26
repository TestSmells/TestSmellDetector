package edu.rit.se.testsmells.testsmell.smell;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import edu.rit.se.testsmells.testsmell.AbstractSmell;
import edu.rit.se.testsmells.testsmell.SmellyElement;
import edu.rit.se.testsmells.testsmell.TestMethod;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/*
Use of Thread.sleep() in test methods can possibly lead to unexpected results as the processing time of tasks on different devices/machines can be different. Use mock objects instead
This code marks a method as smelly if the method body calls Thread.sleep()
 */
public class SleepyTest extends AbstractSmell {

    private List<SmellyElement> smellyElementList;

    public SleepyTest() {
        smellyElementList = new ArrayList<>();
    }

    /**
     * Checks of 'SleepyTest' smell
     */
    @Override
    public String getSmellName() {
        return "Sleepy Test";
    }

    /**
     * Returns true if any of the elements has a smell
     */
    public boolean hasSmell() {
        return smellyElementList.stream().filter(x -> x.hasSmell()).count() >= 1;
    }

    /**
     * Analyze the test file for test methods that use Thread.sleep()
     */
    @Override
    public void runAnalysis(CompilationUnit testFileCompilationUnit, CompilationUnit productionFileCompilationUnit, String testFileName, String productionFileName) throws FileNotFoundException {
        SleepyTest.ClassVisitor classVisitor;
        classVisitor = new SleepyTest.ClassVisitor();
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
        private int sleepCount = 0;
        TestMethod testMethod;

        // examine all methods in the test class
        @Override
        public void visit(MethodDeclaration n, Void arg) {
            if (isValidTestMethod(n)) {
                currentMethod = n;
                testMethod = new TestMethod(n.getNameAsString());
                testMethod.setHasSmell(false); //default value is false (i.e. no smell)
                super.visit(n, arg);

                testMethod.setHasSmell(sleepCount >= 1);
                testMethod.addDataItem("ThreadSleepCount", String.valueOf(sleepCount));

                smellyElementList.add(testMethod);

                //reset values for next method
                currentMethod = null;
                sleepCount = 0;
            }
        }

        // examine the methods being called within the test method
        @Override
        public void visit(MethodCallExpr n, Void arg) {
            super.visit(n, arg);
            if (currentMethod != null) {
                // if the name of a method being called is 'sleep'
                if (n.getNameAsString().equals("sleep")) {
                    //check the scope of the method
                    if ((n.getScope().isPresent() && n.getScope().get() instanceof NameExpr)) {
                        //proceed only if the scope is "Thread"
                        if ((((NameExpr) n.getScope().get()).getNameAsString().equals("Thread"))) {
                            sleepCount++;
                        }
                    }

                }
            }
        }

    }
}
