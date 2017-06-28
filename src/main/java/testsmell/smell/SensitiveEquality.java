package testsmell.smell;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import testsmell.MethodSmell;
import testsmell.ISmell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SensitiveEquality implements ITestSmell {
    List<ISmell> smellList;

    @Override
    public List<ISmell> runAnalysis(CompilationUnit cu) {
        smellList = new ArrayList<>();

        SensitiveEquality.ClassVisitor classVisitor = new SensitiveEquality.ClassVisitor();
        classVisitor.visit(cu,null);

        return smellList;
    }

    @Override
    public String getSmellNameAsString() {
        return "SensitiveEquality";
    }

    private class ClassVisitor extends VoidVisitorAdapter<Void> {
        private MethodDeclaration currentMethod = null;
        private int sensitiveCount =0;
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

                methodSmell.setHasSmell(sensitiveCount > 0);

                map = new HashMap<>();
                map.put("SensitiveCount", String.valueOf(sensitiveCount));
                methodSmell.setSmellData(map);

                smellList.add(methodSmell);

                //reset values for next method
                currentMethod = null;
                sensitiveCount = 0;
            }
        }

        // examine the methods being called within the test method
        @Override
        public void visit(MethodCallExpr n, Void arg) {
            super.visit(n, arg);
            if (currentMethod != null){
                // if the name of a method being called start with 'assert'
                if(n.getNameAsString().startsWith(("assert")) ){
                    // assert methods that contain toString
                    for (Expression argument: n.getArguments()) {
                        if(argument.toString().contains("toString")){
                            sensitiveCount++;
                        }
                    }
                }
                // if the name of a method being called is 'fail'
                else if(n.getNameAsString().equals("fail")){
                    // fail methods that contain toString
                    for (Expression argument: n.getArguments()) {
                        if(argument.toString().contains("toString")){
                            sensitiveCount++;
                        }
                    }
                }

            }
        }

    }
}
