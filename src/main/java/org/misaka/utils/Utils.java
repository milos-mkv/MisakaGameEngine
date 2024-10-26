package org.misaka.utils;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.nfd.NativeFileDialog;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
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

    public static boolean createDirectory(String path) {
        File directory = new File(path);
        if (directory.exists()) {
            return false;
        }
        if (!directory.mkdirs()) {
            return false;
        }
        return true;
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

    public static String openFolderDialog() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer outPathPtr = stack.mallocPointer(1);
            int result = NativeFileDialog.NFD_PickFolder(outPathPtr, (ByteBuffer) null);

            if (result == NativeFileDialog.NFD_OKAY) {
                String folder = MemoryUtil.memUTF8(outPathPtr.get(0));
                System.out.println(folder);
                return folder;
            }
            return null;
        }
    }

    public static float[] matrixToFloatBuffer(Matrix4f matrix) {
        var buffer = new float[16];
        matrix.get(buffer);
        return buffer;
    }

    public static boolean doesDirectoryExists(String path) {
        File directory = new File(path);
        return directory.exists();
    }

    public static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1);
        } else {
            return null; // No extension found
        }
    }

    public static String getFilenameWithoutExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
            filename = filename.substring(0, dotIndex);
        }
        return filename;
    }
}
