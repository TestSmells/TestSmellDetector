package testsmell.smell;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import testsmell.MethodSmell;
import testsmell.ISmell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
This class checks if a test method is empty (i.e. the method does not contain statements in its body)
If the the number of statements in the body is 0, then the method is smelly
 */
public class EmptyTest implements ITestSmell {

    List<ISmell> smellList;

    @Override
    public List<ISmell> runAnalysis(CompilationUnit cu) {
        smellList = new ArrayList<>();

        EmptyTest.ClassVisitor classVisitor = new EmptyTest.ClassVisitor();
        classVisitor.visit(cu, null);

        return smellList;
    }

    private class ClassVisitor extends VoidVisitorAdapter<Void> {
        private MethodDeclaration currentMethod = null;
        private int emptyCount = 0;
        ISmell methodSmell;
        Map<String, String> map;

        // examine all methods in the test class
        @Override
        public void visit(MethodDeclaration n, Void arg) {
            //only analyze methods that either have a @test annotation (Junit 4) or the method name starts with 'test'
            if (n.getAnnotationByName("Test").isPresent() || n.getNameAsString().toLowerCase().startsWith("test")) {
                currentMethod = n;
                methodSmell = new MethodSmell(currentMethod.getNameAsString());
                //get the total number of statements contained in the method
                if (currentMethod.getBody().get().getStatements().size() == 0) {
                    emptyCount++;
                }
            }

            methodSmell.setHasSmell(emptyCount >= 1);

            map = new HashMap<>();
            map.put("EmptyCount", String.valueOf(emptyCount));
            methodSmell.setSmellData(map);

            smellList.add(methodSmell);

            //reset values for next method
            currentMethod = null;
            emptyCount = 0;
        }
    }
}
