package edu.rit.se.testsmells.testsmell;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class TestFile implements SmellsContainer {
    private final String app, testFilePath, productionFilePath;
    private boolean isProductionFileOmitted = false;

    public TestFile(String app, String testFilePath, String productionFilePath) {
        checkValidity(testFilePath, productionFilePath, app);
        this.app = app;
        this.testFilePath = testFilePath;
        this.productionFilePath = productionFilePath;
    }

    /**
     * Apply validation checks on constructor params.
     * It should be overridable for test purpose
     *
     * @param testPath Test file path (must contain file extension and be in a subfolder)
     * @param prodPath Production file path (must contain file extension and be in a subfolder)
     * @param app      Project name (cannot be empty)
     */
    protected void checkValidity(String testPath, String prodPath, String app) {
        if (prodPath.isEmpty()) {
            isProductionFileOmitted = true;
        }
        if (!haveExtension(testPath, prodPath) || !haveFileSeparator(testPath, prodPath)) {
            throw new IllegalArgumentException("Both testFilePath and productionFilePath should include extensions and file separator.");
        }
        if (app.isEmpty()) {
            throw new IllegalArgumentException("App cannot be empty!");
        }
    }

    private boolean haveFileSeparator(String testPath, String prodPath) {
        return testPath.lastIndexOf(File.separator) != -1 && (isProductionFileOmitted || prodPath.lastIndexOf(File.separator) != -1);
    }

    private boolean haveExtension(String testPath, String prodPath) {
        return testPath.lastIndexOf('.') != -1 && (isProductionFileOmitted || prodPath.lastIndexOf('.') != -1);
    }

    /**
     * Retrieve each description property name and getter method in a HashMap
     *
     * @return A Map of description properties
     */
    public Map<String, String> getTestDescriptionEntries() {
        Map<String, String> descriptions = new HashMap<>();

        descriptions.put("App", getApp());
        descriptions.put("TestFileName", getTestFileName());
        descriptions.put("TestFilePath", getTestFilePath());
        descriptions.put("ProductionFilePath", getProductionFilePath());
        descriptions.put("ProductionFileName", getProductionFileName());
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
        if (isProductionFileOmitted) {
            return "";
        }
        return removeExtension(getProductionFileName());
    }

    private String removeExtension(String filename) {
        return filename.substring(0, filename.lastIndexOf("."));
    }

    private String extractFileFromPath(String path) {
        return path.substring(path.lastIndexOf(File.separator) + 1);
    }

    public String getTestFileName() {
        return extractFileFromPath(testFilePath);
    }

    public String getProductionFileName() {
        if (isProductionFileOmitted) {
            return "";
        }
        return extractFileFromPath(productionFilePath);
    }

    /**
     * Returns the path of the test file relative to the folder with the name of the project.
     * If the project directory has a different name, returns an empty string.
     *
     * @return the relative test file path
     */
    public String getRelativeTestFilePath() {
        return extractRelativePathFrom(testFilePath);
    }

    /**
     * Returns the path of the production file relative to the folder with the name of the project.
     * If the project directory has a different name, returns an empty string.
     *
     * @return the relative production file path
     */
    public String getRelativeProductionFilePath() {
        return extractRelativePathFrom(productionFilePath);
    }

    private String extractRelativePathFrom(String path) {
        int projectNameIndex = path.indexOf(app);
        if (projectNameIndex == -1) return "";
        return path.substring(projectNameIndex + app.length() + File.separator.length());
    }
}