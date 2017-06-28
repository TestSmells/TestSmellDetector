package testsmell.smell;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import testsmell.ISmell;
import testsmell.MethodSmell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
This class checks if test methods in the class either catch or throw exceptions. Use Junit's exception handling to automatically pass/fail the test
If this code detects the existence of a catch block or a throw statement in the methods body, the method is marked as smelly
 */
public class ExceptionCatchingThrowing implements ITestSmell  {
    List<ISmell> smellList;

    @Override
    public List<ISmell> runAnalysis(CompilationUnit cu) {
        smellList = new ArrayList<>();

        ExceptionCatchingThrowing.ClassVisitor classVisitor = new ExceptionCatchingThrowing.ClassVisitor();
        classVisitor.visit(cu, null);

        return smellList;
    }

    @Override
    public String getSmellNameAsString() {
        return "ExceptionCatchingThrowing";
    }

    private class ClassVisitor extends VoidVisitorAdapter<Void> {
        private MethodDeclaration currentMethod = null;
        private int exceptionCount = 0;
        ISmell methodSmell;
        Map<String, String> map;


        // examine all methods in the test class
        @Override
        public void visit(MethodDeclaration n, Void arg) {
            //only analyze methods that either have a @test annotation (Junit 4) or the method name starts with 'test'
            if (n.getAnnotationByName("Test").isPresent() || n.getNameAsString().toLowerCase().startsWith("test")) {
                currentMethod = n;
                methodSmell = new MethodSmell(currentMethod.getNameAsString());
                super.visit(n, arg);

                methodSmell.setHasSmell(exceptionCount >= 1);

                map = new HashMap<>();
                map.put("ExceptionCount", String.valueOf(exceptionCount));
                methodSmell.setSmellData(map);

                smellList.add(methodSmell);

                //reset values for next method
                currentMethod = null;
                exceptionCount = 0;
            }
        }


        @Override
        public void visit(ThrowStmt n, Void arg) {
            super.visit(n, arg);

            if(currentMethod!=null){
                exceptionCount++;
            }
        }

        @Override
        public void visit(CatchClause n, Void arg) {
            super.visit(n, arg);

            if(currentMethod!=null){
                exceptionCount++;
            }
        }

    }
}
