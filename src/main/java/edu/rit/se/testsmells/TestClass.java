package edu.rit.se.testsmells;

import java.util.HashMap;
import java.util.Map;

public class TestClass extends SmellyElement {

    private String className;
    private boolean hasSmell;
    private Map<String, String> data;

    public TestClass(String className) {
        this.className = className;
        data = new HashMap<>();
    }

    public void setHasSmell(boolean hasSmell) {
        this.hasSmell = hasSmell;
    }

    public void addDataItem(String name, String value) {
        data.put(name, value);
    }

    @Override
    public String getElementName() {
        return className;
    }

    @Override
    public boolean getHasSmell() {
        return hasSmell;
    }

    @Override
    public Map<String, String> getData() {
        return data;
    }
}
