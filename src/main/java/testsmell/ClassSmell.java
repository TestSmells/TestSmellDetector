package testsmell;

import java.util.Map;

public class ClassSmell implements ISmell {
    private String className;
    private boolean hasSmell;
    private Map<String, String> smellData;

    public ClassSmell(String className) {
        this.className = className;
    }

    public Map<String, String> getSmellData() {
        return smellData;
    }

    public void setSmellData(Map<String, String> data) {
        this.smellData = data;
    }

    public boolean isHasSmell() {
        return hasSmell;
    }

    public void setHasSmell(boolean hasSmell) {
        this.hasSmell = hasSmell;
    }
}
