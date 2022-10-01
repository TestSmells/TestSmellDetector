package testsmell;

import java.util.HashMap;
import java.util.Map;

public class TestMethod extends SmellyElement {

    private final String methodName;
    private boolean hasSmell;
    private final Map<String, String> data;

    public TestMethod(String methodName) {
        this.methodName = methodName;
        data = new HashMap<>();
    }

    public void setSmell(boolean hasSmell) {
        this.hasSmell = hasSmell;
    }

    public void addDataItem(String name, String value) {
        data.put(name, value);
    }

    @Override
    public String getElementName() {
        return methodName;
    }

    @Override
    public boolean isSmelly() {
        return hasSmell;
    }

    @Override
    public Map<String, String> getData() {
        return data;
    }
}
