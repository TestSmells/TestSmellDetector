package testsmell.smell;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import testsmell.ISmell;
import testsmell.MethodSmell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
This class check a test method for the existence of loops and conditional statements in the methods body
 */
public class ConditionalTestLogic implements ITestSmell {
    List<ISmell> smellList;

    @Override
    public List<ISmell> runAnalysis(CompilationUnit cu) {
        smellList = new ArrayList<>();

        ConditionalTestLogic.ClassVisitor classVisitor = new ConditionalTestLogic.ClassVisitor();
        classVisitor.visit(cu, null);

        return smellList;
    }


    private class ClassVisitor extends VoidVisitorAdapter<Void> {
        private MethodDeclaration currentMethod = null;
        private int conditionCount,ifCount,switchCount,forCount,foreachCount,whileCount  = 0;
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

                methodSmell.setHasSmell(conditionCount > 0 | ifCount>0 | switchCount>0 | foreachCount>0 | forCount>0 | whileCount>0);

                map = new HashMap<>();
                map.put("ConditionCount", String.valueOf(conditionCount));
                map.put("IfCount", String.valueOf(ifCount));
                map.put("SwitchCount", String.valueOf(switchCount));
                map.put("ForeachCount", String.valueOf(foreachCount));
                map.put("ForCount", String.valueOf(forCount));
                map.put("WhileCount", String.valueOf(whileCount));
                methodSmell.setSmellData(map);

                smellList.add(methodSmell);

                //reset values for next method
                currentMethod = null;
                conditionCount = 0;
                ifCount=0;
                switchCount=0;
                forCount=0;
                foreachCount=0;
                whileCount=0;
            }
        }


        @Override
        public void visit(IfStmt n, Void arg) {
            super.visit(n, arg);
            if (currentMethod != null) {
                ifCount++;
            }
        }

        @Override
        public void visit(SwitchStmt n, Void arg) {

            super.visit(n, arg);
            if(currentMethod != null) {
                switchCount++;
            }
        }

        @Override
        public void visit(ConditionalExpr n, Void arg) {

            super.visit(n, arg);
            if(currentMethod!=null){
                conditionCount++;
            }
        }

        @Override
        public void visit(ForStmt n, Void arg) {

            super.visit(n, arg);
            if(currentMethod!=null){
                forCount++;
            }
        }

        @Override
        public void visit(ForeachStmt n, Void arg) {
            super.visit(n, arg);
            if(currentMethod!=null){
                foreachCount++;
            }
        }

        @Override
        public void visit(WhileStmt n, Void arg) {
            super.visit(n, arg);
            if(currentMethod !=null){
                whileCount++;
            }
        }
    }

}
