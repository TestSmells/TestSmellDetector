package testsmell;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
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

    public String getTagName(){
        String[] splittud = testFilePath.split(String.format("\\%s", File.separator));
        return splittud[4];
    }

    public String getTestFileName(){
        int lastIndex = testFilePath.lastIndexOf(File.separator);
        return testFilePath.substring(lastIndex+1,testFilePath.length());
    }

    public String getTestFileNameWithoutExtension(){
        int lastIndex = getTestFileName().lastIndexOf(".");
        return getTestFileName().substring(0,lastIndex);
    }

    public String getProductionFileNameWithoutExtension(){
        int lastIndex = getProductionFileName().lastIndexOf(".");
        if(lastIndex==-1)
            return "";
        return getProductionFileName().substring(0,lastIndex);
    }

    public String getProductionFileName(){
        int lastIndex = productionFilePath.lastIndexOf(File.separator);
        if(lastIndex==-1)
            return "";
        return productionFilePath.substring(lastIndex+1,productionFilePath.length());
    }

    public String getRelativeTestFilePath() {
        String[] splitString = testFilePath.split(String.format("\\%s", File.separator));
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            stringBuilder.append(splitString[i] + File.separator);
        }
        return testFilePath.substring(stringBuilder.toString().length()).replace(File.separator, "/");
    }

    public String getRelativeProductionFilePath() {
        if (!StringUtils.isEmpty(productionFilePath)) {
            String[] splitString = productionFilePath.split(String.format("\\%s", File.separator));
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < 5; i++) {
                stringBuilder.append(splitString[i] + File.separator);
            }
            return productionFilePath.substring(stringBuilder.toString().length()).replace(File.separator, "/");
        } else {
            return "";

        }
    }
}