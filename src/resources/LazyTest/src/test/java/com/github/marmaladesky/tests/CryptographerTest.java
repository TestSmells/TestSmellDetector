package com.github.marmaladesky.tests;

import com.github.marmaladesky.Cryptographer;

import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertEquals;

public class CryptographerTest {

    private static final String DECRYPTED_DATA_FILE_4_14 = "src/test/res/rvl_test-0.4.14.xml";
    private static final String ENCRYPTED_DATA_FILE_4_14 = "src/test/res/rvl_test-0.4.14";

    @Test
    public void testDecrypt() throws Exception {
        FileInputStream file = new FileInputStream(ENCRYPTED_DATA_FILE_4_14);
        byte[] enfileData = new byte[file.available()];
        FileInputStream input = new FileInputStream(DECRYPTED_DATA_FILE_4_14);
        byte[] fileData = new byte[input.available()];
        input.read(fileData);
        input.close();
        file.read(enfileData);
        file.close();
        String expectedResult = new String(fileData, "UTF-8");
        assertEquals("Testing simple decrypt",expectedResult, Cryptographer.decrypt(enfileData, "test"));
    }

    @Test
    public void testEncrypt() throws Exception {
        String xml = readFileAsString(DECRYPTED_DATA_FILE_4_14);
        byte[] encrypted = Cryptographer.encrypt(xml, "test");
        String decrypt = Cryptographer.decrypt(encrypted, "test");
        assertEquals(xml, decrypt);
    }

    private String readFileAsString(String filePath) throws IOException {
        StringBuffer fileData = new StringBuffer();
        BufferedReader reader = new BufferedReader(
                new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead;
        while((numRead=reader.read(buf)) != -1){
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
        }
        reader.close();
        return fileData.toString();
    }
}
