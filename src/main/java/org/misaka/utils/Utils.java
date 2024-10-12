package org.misaka.utils;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.PointerBuffer;
import org.lwjgl.util.nfd.NativeFileDialog;

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

    public static String openFileDialog() {
        try {
            var pointerBuffer = PointerBuffer.allocateDirect(1);
            NativeFileDialog.NFD_OpenDialog(pointerBuffer, null, (CharSequence) null);
            return pointerBuffer.getStringASCII().replace("\\", "/");
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }
}
