package testsmell.smell;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import testsmell.ISmell;
import testsmell.MethodSmell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssertionRoulette implements ITestSmell {

    List<ISmell> smellList;

    @Override
    public List<ISmell> runAnalysis(CompilationUnit cu) {
        smellList = new ArrayList<>();

        AssertionRoulette.ClassVisitor classVisitor = new AssertionRoulette.ClassVisitor();
        classVisitor.visit(cu,null);

        return smellList;
    }


    private class ClassVisitor extends VoidVisitorAdapter<Void> {
        private MethodDeclaration currentMethod = null;
        private int assertCount =0;
        ISmell methodSmell;
        Map<String, String> map;

        // examine all methods in the test class
        @Override
        public void visit(MethodDeclaration n, Void arg) {
            //only analyze methods that either have a @test annotation (Junit 4) or the method name starts with 'test'
            if(n.getAnnotationByName("Test").isPresent() || n.getNameAsString().toLowerCase().startsWith("test") ){
                currentMethod = n;
                methodSmell = new MethodSmell(currentMethod.getNameAsString());
                super.visit(n, arg);

                methodSmell.setHasSmell(assertCount > 1);

                map = new HashMap<>();
                map.put("AssertCount", String.valueOf(assertCount));
                methodSmell.setSmellData(map);

                smellList.add(methodSmell);

                //reset values for next method
                currentMethod = null;
                assertCount = 0;
            }
        }

        // examine the methods being called within the test method
        @Override
        public void visit(MethodCallExpr n, Void arg) {
            super.visit(n, arg);
            if (currentMethod != null){
                // if the name of a method being called start with 'assert'
                if(n.getNameAsString().startsWith(("assert")) ){
                    // assert methods that do not contain a message
                    if(n.getArguments().size() < 3){
                        assertCount++;
                    }
                }
                // if the name of a method being called is 'fail'
                else if(n.getNameAsString().equals("fail")){
                    // fail method does not contain a message
                    if(n.getArguments().size() < 1){
                        assertCount++;
                    }
                }

            }
        }

    }
}

