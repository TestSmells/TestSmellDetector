package edu.rit.se.testsmells.testsmell;

import java.util.HashMap;
import java.util.Map;

public class TestClass extends SmellyElement {

    public TestClass(String name) {
        super(name);
    }

    @Override
    public Map<String, String> getTestDescriptionEntries() {
        Map<String, String> entries = new HashMap<>(getData());

        entries.put("Name", getElementName());

        return entries;
    }
}
