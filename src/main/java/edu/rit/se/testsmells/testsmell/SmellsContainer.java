package edu.rit.se.testsmells.testsmell;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public interface SmellsContainer {
    List<AbstractSmell> testSmells = new ArrayList<>();

    Map<String, String> getTestDescriptionEntries();

    default void addDetectedSmell(AbstractSmell newSmell) {
        assert Objects.nonNull(newSmell);
        AbstractSmell existingSmell = findSmellNamed(newSmell.getSmellName());
        if (Objects.nonNull(existingSmell)) {
            for (SmellyElement smellyElement : newSmell.getSmellyElements()) {
                existingSmell.addSmellyElement(smellyElement);
            }
        } else {
            testSmells.add(newSmell);
        }
    }

    default AbstractSmell findSmellNamed(String smellName) {
        for (AbstractSmell existingSmell : testSmells) {
            if (smellName.equals(existingSmell.getSmellName())) {
                return existingSmell;
            }
        }
        return null;
    }

    default List<AbstractSmell> getTestSmells() {
        return testSmells;
    }
}
