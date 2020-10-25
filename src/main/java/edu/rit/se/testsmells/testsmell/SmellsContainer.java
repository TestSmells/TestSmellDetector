package edu.rit.se.testsmells.testsmell;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

public abstract class SmellsContainer {
    public final Set<AbstractSmell> testSmells = new CopyOnWriteArraySet<>();

    public abstract Map<String, String> getTestDescriptionEntries();

    public void addDetectedSmell(AbstractSmell newSmell) {
        assert Objects.nonNull(newSmell);
        testSmells.add(newSmell);
    }

    public List<AbstractSmell> getTestSmells() {
        return new ArrayList<>(testSmells);
    }
}
