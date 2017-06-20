package testsmell;

import java.util.Map;

public interface ISmell {

    Map<String, String> getSmellData();

    void setSmellData(Map<String, String> data);

    boolean isHasSmell();

    void setHasSmell(boolean hasSmell);
}
