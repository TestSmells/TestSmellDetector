package testsmell.core;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProjectMonitoringInputGenerator {
    private final List<File> sourceFileList = new ArrayList<>();
    private final List<String> inputList = new ArrayList<>();
    private final StringBuilder text = new StringBuilder();

    public ProjectMonitoringInputGenerator(File directory) throws IOException {

        sourceFileList.addAll(FileUtils.listFiles(new File(directory, "src/test"), "java".split(","), true));
        for (File sourceFile : sourceFileList) {
            String localSource = sourceFile.getPath().substring(directory.getPath().length()).replace("\\", "/").replace("/src/test/java/", "");
            localSource = localSource.replace("Test.java", ".java");
            File file = new File(directory, "src/main/java/" + localSource);

            String line;
            if (file.exists()) {
                line = directory.getName() + "," + sourceFile.getPath().replace("\\", "/") + "," + file.getPath().replace("\\", "/");
            } else {
                line = directory.getName() + "," + sourceFile.getPath().replace("\\", "/");
            }
            inputList.add(line);
            text.append(line).append("\n");
        }
    }

    public List<String> getInputList() {
        return inputList;
    }

    public String getText() {
        return text.toString();
    }
}
