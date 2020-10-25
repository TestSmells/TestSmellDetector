package edu.rit.se.testsmells.testsmell;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

public interface SmellsContainer {
    Set<AbstractSmell> testSmells = new CopyOnWriteArraySet<>();

    Map<String, String> getTestDescriptionEntries();

    default void addDetectedSmell(AbstractSmell newSmell) {
        assert Objects.nonNull(newSmell);
        testSmells.add(newSmell);
    }

    default List<AbstractSmell> getTestSmells() {
        return new ArrayList<>(testSmells);
    }
}
