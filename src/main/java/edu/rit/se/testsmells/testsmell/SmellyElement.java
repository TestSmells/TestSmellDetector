package edu.rit.se.testsmells.testsmell;

import java.util.HashMap;
import java.util.Map;

public abstract class SmellyElement {
    protected String name;
    private Map<String, String> data;
    private boolean hasSmell;

    public SmellyElement(String name) {
        data = new HashMap<>();
        this.name = name;
    }

    public void setHasSmell(boolean hasSmell) {
        this.hasSmell = hasSmell;
    }

    public void addDataItem(String name, String value) {
        data.put(name, value);
    }

    public String getElementName() {
        return name;
    }

    public boolean hasSmell() {
        return hasSmell;
    }

    public Map<String, String> getData() {
        return data;
    }
}
