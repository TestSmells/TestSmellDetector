package testsmell;

import com.github.javaparser.ast.CompilationUnit;
import thresholds.Thresholds;

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractSmell {
    protected Thresholds thresholds;
    protected Set<SmellyElement> smellyElementsSet;

    public AbstractSmell(Thresholds thresholds) {
        this.thresholds = thresholds;
        this.smellyElementsSet = new HashSet<>();
    }

    public abstract String getSmellName();

    /**
     * Return 1 if any of the elements has a smell; 0 otherwise
     */
    public boolean hasSmell() {
        return smellyElementsSet.stream().filter(SmellyElement::isSmelly).count() >= 1;
    }

    public abstract void runAnalysis(CompilationUnit testFileCompilationUnit,
                                     CompilationUnit productionFileCompilationUnit,
                                     String testFileName,
                                     String productionFileName) throws FileNotFoundException;

    /**
     * Returns the set of analyzed elements (i.e. test methods)
     */
    public Set<SmellyElement> getSmellyElements() {
        return smellyElementsSet;
    }

    /**
     * Returns the number of test cases in a test suite (jUnit test file).
     * In theory, it counts all the smelly elements (i.e., the methods), that are smelly
     */
    public int getNumberOfSmellyTests() {
        return (int) smellyElementsSet.stream().filter(SmellyElement::isSmelly).count();
    }
}
