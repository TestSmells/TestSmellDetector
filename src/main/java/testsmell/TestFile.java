package testsmell;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

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

    public String getRelativeTestFilePath() {
        String[] splitString = testFilePath.split("\\\\");
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            stringBuilder.append(splitString[i] + "\\");
        }
        return testFilePath.substring(stringBuilder.toString().length()).replace("\\", "/");
    }

    public String getRelativeProductionFilePath() {
        if (!StringUtils.isEmpty(productionFilePath)) {
            String[] splitString = productionFilePath.split("\\\\");
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < 5; i++) {
                stringBuilder.append(splitString[i] + "\\");
            }
            return productionFilePath.substring(stringBuilder.toString().length()).replace("\\", "/");
        } else {
            return "";

        }
    }
}