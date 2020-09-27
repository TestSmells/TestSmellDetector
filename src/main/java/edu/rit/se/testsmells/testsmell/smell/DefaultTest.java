package edu.rit.se.testsmells.testsmell.smell;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import edu.rit.se.testsmells.testsmell.AbstractSmell;
import edu.rit.se.testsmells.testsmell.TestClass;

import java.io.FileNotFoundException;

/*
By default Android Studio creates default test classes when a project is created. These classes are meant to serve as an example for developers when wring unit tests
This code marks the class as smelly if the class name corresponds to the name of the default test classes
 */
public class DefaultTest extends AbstractSmell {


    private CompilationUnit testFileCompilationUnit;

    public DefaultTest() {
        super();
    }

    /**
     * Checks of 'Default Test' smell
     */
    @Override
    public String getSmellName() {
        return "Default Test";
    }

    @Override
    public void runAnalysis(CompilationUnit testFileCompilationUnit, CompilationUnit productionFileCompilationUnit, String testFileName, String productionFileName) throws FileNotFoundException {
        DefaultTest.ClassVisitor classVisitor;
        classVisitor = new DefaultTest.ClassVisitor();
        this.testFileCompilationUnit = testFileCompilationUnit;
        classVisitor.visit(this.testFileCompilationUnit, null);
    }

    private class ClassVisitor extends VoidVisitorAdapter<Void> {
        TestClass testClass;

        @Override
        public void visit(ClassOrInterfaceDeclaration n, Void arg) {
            if (n.getNameAsString().equals("ExampleUnitTest") || n.getNameAsString().equals("ExampleInstrumentedTest")) {
                testClass = new TestClass(getFullClassName(testFileCompilationUnit, n));
                testClass.setHasSmell(true);
                addSmellyElement(testClass);
            }
            super.visit(n, arg);
        }
    }
}
