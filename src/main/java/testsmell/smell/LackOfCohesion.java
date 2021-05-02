package testsmell.smell;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import testsmell.AbstractSmell;
import testsmell.SmellyElement;
import testsmell.TestClass;
import testsmell.TestMethod;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;


public class LackOfCohesion extends AbstractSmell {

    private List<SmellyElement> smellyElementList;
    private String testFileName;

    @Override
    public String getSmellName() { return "Lack of Cohesion"; }

    @Override
    public boolean getHasSmell() {
        return smellyElementList.stream().filter(x -> x.getHasSmell()).count() >= 1;
    }

    @Override
    public void runAnalysis(CompilationUnit testFileCompilationUnit, CompilationUnit productionFileCompilationUnit, String testFileName, String productionFileName) throws FileNotFoundException {
        this.testFileName = testFileName;
        LackOfCohesion.ClassVisitor classVisitor;
        classVisitor = new LackOfCohesion.ClassVisitor();
        classVisitor.visit(testFileCompilationUnit, null);
    }

    @Override
    public List<SmellyElement> getSmellyElements() {
        return smellyElementList;
    }


    private class ClassVisitor extends VoidVisitorAdapter<List<String>> {

        @Override
        public void visit(MethodDeclaration md, List<String> collector) {
            super.visit(md, collector);
            collector.add(md.getNameAsString());
        }

    }

}
