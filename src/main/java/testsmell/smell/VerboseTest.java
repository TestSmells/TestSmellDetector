package testsmell.smell;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import testsmell.ISmell;
import testsmell.MethodSmell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
If a test methods contains a statements that exceeds a certain threshold, the method is marked as smelly
 */
public class VerboseTest implements ITestSmell {

    List<ISmell> smellList;


    @Override
    public List<ISmell> runAnalysis(CompilationUnit cu) {
        smellList = new ArrayList<>();

        VerboseTest.ClassVisitor classVisitor = new VerboseTest.ClassVisitor();
        classVisitor.visit(cu, null);

        return smellList;
    }

    private class ClassVisitor extends VoidVisitorAdapter<Void> {
        final int MAX_STATEMENTS =   123;
        private MethodDeclaration currentMethod = null;
        private int verboseCount = 0;
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
                if (currentMethod.getBody().get().getStatements().size() >= MAX_STATEMENTS) {
                    verboseCount++;
                }
            }

            methodSmell.setHasSmell(verboseCount > 1);

            map = new HashMap<>();
            map.put("VerboseCount", String.valueOf(verboseCount));
            methodSmell.setSmellData(map);

            smellList.add(methodSmell);

            //reset values for next method
            currentMethod = null;
            verboseCount = 0;
        }
    }
}
