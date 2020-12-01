package testsmell;

import com.github.javaparser.ast.CompilationUnit;
import thresholds.Thresholds;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSmell {
    protected Thresholds thresholds;
    protected List<SmellyElement> smellyElementList;

    public AbstractSmell(Thresholds thresholds) {
        this.thresholds = thresholds;
        this.smellyElementList = new ArrayList<>();
    }

    public abstract String getSmellName();

    /**
     * Return 1 if any of the elements has a smell; 0 otherwise
     */
    public int getHasSmell() {
        boolean isSmelly = smellyElementList.stream().filter(x -> x.getHasSmell()).count() >= 1;
        return isSmelly ? 1 : 0;
    }

    public abstract void runAnalysis(CompilationUnit testFileCompilationUnit,
                                     CompilationUnit productionFileCompilationUnit,
                                     String testFileName,
                                     String productionFileName) throws FileNotFoundException;

    public abstract List<SmellyElement> getSmellyElements();

    public abstract int getNumberOfSmellyTests();
}
