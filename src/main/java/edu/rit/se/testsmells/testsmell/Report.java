package edu.rit.se.testsmells.testsmell;

import java.util.List;

public interface Report {
    List<String> getEntryValues();

    List<String> getEntryKeys();

    String getValue(String key);
}
