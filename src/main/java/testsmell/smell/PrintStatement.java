package testsmell.smell;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import testsmell.ISmell;
import testsmell.MethodSmell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
Test methods should not contain print statements as execution of unit tests is an automated process with little to no human intervention. Hence, print statements are redundant.
This code checks the body of each test method if System.out. print(), println(), printf() and write() methods are called
 */
public class PrintStatement implements ITestSmell {

    List<ISmell> smellList;

    @Override
    public List<ISmell> runAnalysis(CompilationUnit cu) {
        smellList = new ArrayList<>();

        PrintStatement.ClassVisitor classVisitor = new PrintStatement.ClassVisitor();
        classVisitor.visit(cu, null);

        return smellList;
    }

    @Override
    public String getSmellNameAsString() {
        return "PrintStatement";
    }

    private class ClassVisitor extends VoidVisitorAdapter<Void> {
        private MethodDeclaration currentMethod = null;
        private int printCount =0;
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

                methodSmell.setHasSmell(printCount >= 1);

                map = new HashMap<>();
                map.put("PrintCount", String.valueOf(printCount));
                methodSmell.setSmellData(map);

                smellList.add(methodSmell);

                //reset values for next method
                currentMethod = null;
                printCount = 0;
            }
        }

        // examine the methods being called within the test method
        @Override
        public void visit(MethodCallExpr n, Void arg) {
            super.visit(n, arg);
            if (currentMethod != null){
                // if the name of a method being called is 'print' or 'println' or 'printf' or 'write'
                if(n.getNameAsString().equals("print") || n.getNameAsString().equals("println") || n.getNameAsString().equals("printf")|| n.getNameAsString().equals("write")){
                    //check the scope of the method & proceed only if the scope is "out"
                    if((n.getScope().isPresent() &&
                            n.getScope().get() instanceof FieldAccessExpr &&
                            (((FieldAccessExpr) n.getScope().get())).getNameAsString().equals("out"))){

                        FieldAccessExpr f1  = (((FieldAccessExpr) n.getScope().get()));

                        //check the scope of the field & proceed only if the scope is "System"
                        if((f1.getScope() != null &&
                                f1.getScope() instanceof NameExpr &&
                                ((NameExpr) f1.getScope()).getNameAsString().equals("System"))){
                            //a print statement exists in the method body
                            printCount++;
                        }
                    }

                }
            }
        }

    }
}
