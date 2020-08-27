package edu.rit.se.testsmells.testsmell;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class SmellsContainer {
    protected final List<AbstractSmell> testSmells;

    public SmellsContainer() {
        this.testSmells = new ArrayList<>();
    }

    public abstract Map<String, String> getTestDescriptionEntries();

    public void addDetectedSmell(AbstractSmell smell) {
        testSmells.add(smell);
    }

    public List<AbstractSmell> getTestSmells() {
        return testSmells;
    }
}
