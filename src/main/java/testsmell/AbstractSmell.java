package testsmell;

import java.io.FileNotFoundException;
import java.util.List;

public abstract class AbstractSmell {
    public abstract String getSmellName();

    public abstract boolean getHasSmell();

    public abstract void runAnalysis(String testFilePath, String productionFilePath) throws FileNotFoundException;

    public abstract List<SmellyElement> getSmellyElements();
}
