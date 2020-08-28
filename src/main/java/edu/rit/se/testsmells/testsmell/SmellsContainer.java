package edu.rit.se.testsmells.testsmell;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface SmellsContainer {
    List<AbstractSmell> testSmells = new ArrayList<>();

    public abstract Map<String, String> getTestDescriptionEntries();

    default void addDetectedSmell(AbstractSmell smell) {
        testSmells.add(smell);
    }

    default List<AbstractSmell> getTestSmells() {
        return testSmells;
    }
}
