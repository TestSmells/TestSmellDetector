package edu.rit.se.testsmells.testsmell;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class TestFile extends SmellsContainer {
    private final String app, testFilePath, productionFilePath;

    public TestFile(String app, String testFilePath, String productionFilePath) {
        super();
        this.app = app;
        this.testFilePath = testFilePath;
        this.productionFilePath = productionFilePath;
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

    public String getApp() {
        return app;
    }

    public String getProductionFilePath() {
        return productionFilePath;
    }

    public String getTestFilePath() {
        return testFilePath;
    }

    public String getTestFileNameWithoutExtension() {
        return removeExtension(getTestFileName());
    }

    public String getProductionFileNameWithoutExtension() {
        return removeExtension(getProductionFileName());
    }

    private String removeExtension(String filename) {
        try {
            return filename.substring(0, filename.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            return filename;
        }
    }

    private String extractFileFromPath(String path) {
        return path.substring(path.lastIndexOf(File.separator) + 1);
    }

    public String getTestFileName() {
        return extractFileFromPath(testFilePath);
    }

    public String getProductionFileName() {
        return extractFileFromPath(productionFilePath);
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