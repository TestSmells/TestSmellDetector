package testsmell;

import java.util.Map;

public class MethodSmell implements ISmell {
    private String methodName;
    private boolean hasSmell;
    private Map<String, String> smellData;

    public MethodSmell(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodName() {
        return methodName;
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
