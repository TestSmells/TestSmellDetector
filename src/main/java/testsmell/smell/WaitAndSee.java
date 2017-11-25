package testsmell.smell;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import testsmell.AbstractSmell;
import testsmell.SmellyElement;
import testsmell.TestMethod;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/*
Use of Thread.sleep() in test methods can possibly lead to unexpected results as the processing time of tasks on different devices/machines can be different. Use mock objects instead
This code marks a method as smelly if the method body calls Thread.sleep()
 */
public class WaitAndSee extends AbstractSmell {

    private List<SmellyElement> smellyElementList;

    public WaitAndSee() {
        smellyElementList = new ArrayList<>();
    }

    /**
     * Checks of 'Wait And See' smell
     */
    @Override
    public String getSmellName() {
        return "Wait And See";
    }

    /**
     * Returns true if any of the elements has a smell
     */
    @Override
    public boolean getHasSmell() {
        return smellyElementList.stream().filter(x -> x.getHasSmell()).count() >= 1;
    }

    /**
     * Analyze the test file for test methods that use Thread.sleep()
     */
    @Override
    public void runAnalysis(CompilationUnit testFileCompilationUnit,CompilationUnit productionFileCompilationUnit, String testFileName, String productionFileName) throws FileNotFoundException {
        WaitAndSee.ClassVisitor classVisitor;
        classVisitor = new WaitAndSee.ClassVisitor();
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
            if (!n.getAnnotationByName("Ignore").isPresent()) {
                //only analyze methods that either have a @test annotation (Junit 4) or the method name starts with 'test'
                if (n.getAnnotationByName("Test").isPresent() || n.getNameAsString().toLowerCase().startsWith("test")) {
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
