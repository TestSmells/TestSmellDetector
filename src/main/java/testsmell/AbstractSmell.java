package testsmell;

import com.github.javaparser.ast.CompilationUnit;

import java.io.FileNotFoundException;
import java.util.List;

public abstract class AbstractSmell {
    public abstract String getSmellName();

    public abstract boolean getHasSmell();

    //public abstract void runAnalysis(String testFilePath, String productionFilePath) throws FileNotFoundException;

    public abstract void runAnalysis(CompilationUnit testFileCompilationUnit,CompilationUnit productionFileCompilationUnit) throws FileNotFoundException;

    public abstract List<SmellyElement> getSmellyElements();
}
