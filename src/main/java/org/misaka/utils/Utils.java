package org.misaka.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class Utils {

    public static void saveToFile(Path filePath, String data) {
        try {
            Files.write(filePath, data.getBytes());
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public static String readFromFile(Path filePath) {
        String content = null;
        try {
            content = Files.readString(filePath, StandardCharsets.UTF_8);
        } catch (IOException e)  {
            System.out.println(e);
        }
        return content;
    }
}
