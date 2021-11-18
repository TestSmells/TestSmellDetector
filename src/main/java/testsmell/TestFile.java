package testsmell;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TestFile {
    private final String app, testFilePath, productionFilePath;
    private final List<AbstractSmell> testSmells;
    private int numberOfTestMethods = 0;

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

    /**
     * Supposed to return the version of the project.
     * Returns the "N.I.Y", Not Implemented Yet string
     * todo: not implemented in any way yet
     */
    public String getTagName() {
        return "N.I.Y";
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
        if (lastIndex == -1)
            return "";
        return getProductionFileName().substring(0, lastIndex);
    }

    public String getProductionFileName() {
        int lastIndex = productionFilePath.lastIndexOf(File.separator);
        if (lastIndex == -1)
            return "";
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
            if (projectNameIndex == -1)
                return "";
            return testFilePath.substring(projectNameIndex + app.length() + File.separator.length());
        } else
            return "";
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
            if (projectNameIndex == -1)
                return "";
            return productionFilePath.substring(projectNameIndex + app.length() + File.separator.length());
        } else
            return "";
    }

    /**
     * Returns the number of test methods in a test suite
     */
    public int getNumberOfTestMethods() {
        return numberOfTestMethods;
    }

    /**
     * Sets the number of test methods in a test suite
     * @param numberOfTestMethods
     */
    public void setNumberOfTestMethods(int numberOfTestMethods) {
        this.numberOfTestMethods = numberOfTestMethods;
    }
}
