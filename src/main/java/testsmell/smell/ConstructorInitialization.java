package testsmell.smell;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import testsmell.ClassSmell;
import testsmell.ISmell;

import java.util.ArrayList;
import java.util.List;


/*
This class checks if the code file contains a Constructor. Ideally, the test suite should not have a constructor. Initialization of fields should be in the setUP() method
If this code detects the existence, it sets the class as smelly
 */
public class ConstructorInitialization implements ITestSmell{
    List<ISmell> smellList;


    @Override
    public List<ISmell> runAnalysis(CompilationUnit cu) {
        smellList = new ArrayList<>();

        ConstructorInitialization.ClassVisitor classVisitor = new ConstructorInitialization.ClassVisitor();
        classVisitor.visit(cu, null);

        return smellList;
    }

    private class ClassVisitor extends VoidVisitorAdapter<Void> {
        ISmell classSmell;

        @Override
        public void visit(ConstructorDeclaration n, Void arg) {
            //if(n.getBody().getStatements().size()>=1){ //uncomment this condition if we need to check if we need to indicate a smell only if the constructor has code statements in it
               classSmell = new ClassSmell(n.getNameAsString());
               classSmell.setHasSmell(true);
               smellList.add(classSmell);
            //}
        }
    }
}
