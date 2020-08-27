package edu.rit.se.testsmells.testsmell;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestFile {
    private final String app, testFilePath, productionFilePath;
    private final List<AbstractSmell> testSmells;

    public TestFile(String app, String testFilePath, String productionFilePath) {
        this.app = app;
        this.testFilePath = testFilePath;
        this.productionFilePath = productionFilePath;
        this.testSmells = new ArrayList<>();
    }

    public Map<String, String> getTestDescriptionEntries() {
        Map<String, String> descriptions = new HashMap<>();

        descriptions.put("App", getApp());
        descriptions.put("TestFileName", getTestFileName());
        descriptions.put("TestFilePath", getTestFilePath());
        descriptions.put("ProductionFilePath", getProductionFilePath());
        descriptions.put("RelativeTestFilePath", getRelativeTestFilePath());
        descriptions.put("RelativeProductionFilePath", getRelativeProductionFilePath());

        return descriptions;
    }

    public void addDetectedSmell(AbstractSmell smell) {
        testSmells.add(smell);
    }

    public List<AbstractSmell> getTestSmells() {
        return testSmells;
    }

    public String getApp() {
        return app;
    }

    public String getProductionFilePath() {
        return productionFilePath;
    }

    public String getTestFilePath() {
        return testFilePath;
    }

    public String getTestFileName() {
        int lastIndex = testFilePath.lastIndexOf(File.separator);
        return testFilePath.substring(lastIndex + 1);
    }

    public String getTestFileNameWithoutExtension() {
        int lastIndex = getTestFileName().lastIndexOf(".");
        return getTestFileName().substring(0, lastIndex);
    }

    public String getProductionFileNameWithoutExtension() {
        int lastIndex = getProductionFileName().lastIndexOf(".");
        if (lastIndex == -1) return "";
        return getProductionFileName().substring(0, lastIndex);
    }

    public String getProductionFileName() {
        int lastIndex = productionFilePath.lastIndexOf(File.separator);
        if (lastIndex == -1) return "";
        return productionFilePath.substring(lastIndex + 1);
    }

    /**
     * Returns the path of the test file relative to the folder with the name of the project.
     * If the project directory has a different name, returns an empty string.
     *
     * @return the relative test file path
     */
    public String getRelativeTestFilePath() {
        if (!StringUtils.isEmpty(testFilePath)) {
            int projectNameIndex = testFilePath.lastIndexOf(app);
            if (projectNameIndex == -1) return "";
            return testFilePath.substring(projectNameIndex + app.length() + File.separator.length());
        } else return "";
    }

    /**
     * Returns the path of the production file relative to the folder with the name of the project.
     * If the project directory has a different name, returns an empty string.
     *
     * @return the relative production file path
     */
    public String getRelativeProductionFilePath() {
        if (!StringUtils.isEmpty(productionFilePath)) {
            int projectNameIndex = productionFilePath.lastIndexOf(app);
            if (projectNameIndex == -1) return "";
            return productionFilePath.substring(projectNameIndex + app.length() + File.separator.length());
        } else return "";
    }
}