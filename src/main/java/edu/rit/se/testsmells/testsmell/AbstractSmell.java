package edu.rit.se.testsmells.testsmell;

import com.github.javaparser.ast.CompilationUnit;

import java.io.FileNotFoundException;
import java.util.List;

public interface AbstractSmell {
    String getSmellName();

    boolean hasSmell();

    void runAnalysis(CompilationUnit testFileCompilationUnit, CompilationUnit productionFileCompilationUnit, String testFileName, String productionFileName) throws FileNotFoundException;

    List<SmellyElement> getSmellyElements();
}
