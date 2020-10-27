package edu.rit.se.testsmells.testsmell.smell;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import edu.rit.se.testsmells.testsmell.AbstractSmell;
import edu.rit.se.testsmells.testsmell.TestClass;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;


/*
This class checks if the code file contains a Constructor. Ideally, the test suite should not have a constructor. Initialization of fields should be in the setUP() method
If this code detects the existence of a constructor, it sets the class as smelly
 */
public class ConstructorInitialization extends AbstractSmell {
    private String testFileName;
    private CompilationUnit testFileCompilationUnit;

    public ConstructorInitialization() {
        super();
    }

    @Override
    public AbstractSmell recreate() {
        return new ConstructorInitialization();
    }

    /**
     * Checks of 'Constructor Initialization' smell
     */
    @Override
    public String getSmellName() {
        return "Constructor Initialization";
    }

    /**
     * Analyze the test file for Constructor Initialization smell
     */
    @Override
    public void runAnalysis(CompilationUnit testFileCompilationUnit, CompilationUnit productionFileCompilationUnit, String testFileName, String productionFileName) throws FileNotFoundException {
        this.testFileName = testFileName;
        ConstructorInitialization.ClassVisitor classVisitor;
        classVisitor = new ConstructorInitialization.ClassVisitor();
        this.testFileCompilationUnit = testFileCompilationUnit;
        classVisitor.visit(this.testFileCompilationUnit, null);
    }


    private class ClassVisitor extends VoidVisitorAdapter<Void> {
        TestClass testClass;
        boolean constructorAllowed = false;

        @Override
        public void visit(ClassOrInterfaceDeclaration n, Void arg) {
            for (int i = 0; i < n.getExtendedTypes().size(); i++) {
                ClassOrInterfaceType node = n.getExtendedTypes().get(i);
                constructorAllowed = node.getNameAsString().equals("ActivityInstrumentationTestCase2");
            }
            testClass = new TestClass(getFullClassName(testFileCompilationUnit, n));
            testClass.setHasSmell(false);
            super.visit(n, arg);
            addSmellyElement(testClass);
        }

        @Override
        public void visit(ConstructorDeclaration n, Void arg) {
            // This check is needed to handle java files that have multiple classes
            if (n.getNameAsString().equals(testFileName)) {
                testClass.setHasSmell(!constructorAllowed);
            }
        }
    }
}
