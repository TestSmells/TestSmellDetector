package testsmell;

import thresholds.Thresholds;

@FunctionalInterface
public interface SmellFactory {
    AbstractSmell createInstance(Thresholds thresholds);
}
