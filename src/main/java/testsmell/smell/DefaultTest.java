package testsmell.smell;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import testsmell.ClassSmell;
import testsmell.ISmell;

import java.util.ArrayList;
import java.util.List;
/*
By default Android Studio creates default test classes when a project is created. These classes are meant to serve as an example for developers when wring unit tests
This code marks the class as smelly if the class name corresponds to the name of the default test classes
 */
public class DefaultTest implements ITestSmell {

    List<ISmell> smellList;
    ISmell classSmell;

    @Override
    public List<ISmell> runAnalysis(CompilationUnit cu) {
        smellList = new ArrayList<>();

        DefaultTest.ClassVisitor classVisitor = new DefaultTest.ClassVisitor();
        classVisitor.visit(cu, null);

        return smellList;
    }

    private class ClassVisitor extends VoidVisitorAdapter<Void> {

        @Override
        public void visit(ClassOrInterfaceDeclaration n, Void arg) {
            if(n.getNameAsString().equals("ExampleUnitTest") || n.getNameAsString().equals("ExampleInstrumentedTest"))
            {
                classSmell = new ClassSmell(n.getNameAsString());
                classSmell.setHasSmell(true);
                smellList.add(classSmell);
            }
            super.visit(n, arg);
        }
    }
}
