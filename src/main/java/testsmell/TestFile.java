package testsmell;

import testsmell.smell.EagerTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TestFile {
    private String app, testFilePath, productionFilePath;
    private List<AbstractSmell> testSmells;

    public String getApp() {
        return app;
    }

    public String getProductionFilePath() {
        return productionFilePath;
    }

    public String getTestFilePath() {
        return testFilePath;
    }

    public List<AbstractSmell> getTestSmells() {
        return testSmells;
    }

    public boolean getHasProductionFile() {
        return ((productionFilePath != null && !productionFilePath.isEmpty()));
    }

    public TestFile(String app, String testFilePath, String productionFilePath) {
        this.app = app;
        this.testFilePath = testFilePath;
        this.productionFilePath = productionFilePath;
        this.testSmells = new ArrayList<>();
    }

    public void addSmell(AbstractSmell smell) {
        testSmells.add(smell);
    }

}
