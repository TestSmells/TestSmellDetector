import testsmell.TestSmellDetector;

import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        TestSmellDetector testSmellDetector = new TestSmellDetector();
        //testSmellDetector.detectSmells("C:\\Projects\\TestSmells_ExisitngTools\\samples\\ar.rulosoft.mimanganu\\app\\src\\androidTest\\java\\ar\\rulosoft\\mimanganu\\TestServers.java");
        //testSmellDetector.detectSmells("C:\\Projects\\TestSmells_ExisitngTools\\samples\\org.xbmc.kore\\app\\src\\test\\java\\org\\xbmc\\kore\\provider\\mediaprovider\\AlbumsTest.java");
        testSmellDetector.detectSmells("C:\\Projects\\TestSmells\\TestSmellDetector\\src\\main\\java\\testsmell\\smell\\EmptyTest.java");
        System.out.println("end");
    }
}
