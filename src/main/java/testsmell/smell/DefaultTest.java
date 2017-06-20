package testsmell.smell;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import testsmell.ClassSmell;
import testsmell.ISmell;

import java.util.ArrayList;
import java.util.List;

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
