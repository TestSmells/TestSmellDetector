package edu.rit.se.testsmells;

import com.github.javaparser.ast.CompilationUnit;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSmell {

    protected List<SmellyElement> smellyElementList;

    public AbstractSmell() {
        smellyElementList = new ArrayList<>();
    }

    /**
     * Returns true if any of the elements has a smell
     */
    public boolean getHasSmell() {
        return smellyElementList.stream().filter(x -> x.getHasSmell()).count() >= 1;
    }

    /**
     * Returns the set of analyzed elements (i.e. test methods)
     */
    public List<SmellyElement> getSmellyElements() {
        return smellyElementList;
    }

    public abstract String getSmellName();

    public abstract void runAnalysis(CompilationUnit testFileCompilationUnit,CompilationUnit productionFileCompilationUnit, String testFileName, String productionFileName) throws FileNotFoundException;
}
