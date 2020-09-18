package edu.rit.se.testsmells.testsmell;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public interface SmellsContainer {
    List<AbstractSmell> testSmells = new ArrayList<>();

    Map<String, String> getTestDescriptionEntries();

    default void addDetectedSmell(AbstractSmell newSmell) {
        if (Objects.nonNull(newSmell)) {
            AbstractSmell existingSmell = findSameTypeSmell(newSmell);
            if (Objects.nonNull(existingSmell)) {
                for (SmellyElement smellyElement : newSmell.getSmellyElements()) {
                    existingSmell.addSmellyElement(smellyElement);
                }
                return;
            }
        }
        testSmells.add(newSmell);
    }

    default AbstractSmell findSameTypeSmell(AbstractSmell smellType) {
        for (AbstractSmell existingSmell : testSmells) {
            if (Objects.nonNull(existingSmell) && smellType.getClass().equals(existingSmell.getClass())) {
                return existingSmell;
            }
        }
        return null;
    }

    default List<AbstractSmell> getTestSmells() {
        return testSmells;
    }
}
