package edu.rit.se.testsmells.testsmell;

import java.util.List;

/**
 * Report informations extractor
 */
public interface ExtractingStrategy {
    List<Report> extract(List<AbstractSmell> smells, Class<?> type);
}
