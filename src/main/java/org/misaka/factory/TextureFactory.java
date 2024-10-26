package org.misaka.factory;

import lombok.Getter;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.misaka.gfx.Texture;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public abstract class TextureFactory {

    @Getter
    private static final Map<Path, Texture> loadedTextures = new HashMap<>();

    public static Texture createTexture(Path path) {

        if (loadedTextures.containsKey(path)) {
            return loadedTextures.get(path);
        }

        try (var stack = MemoryStack.stackPush()) {
            STBImage.stbi_set_flip_vertically_on_load(true);
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer noc = stack.mallocInt(1);

            ByteBuffer data = STBImage.stbi_load(path.toString(), w, h, noc, 0);

            if (data == null) {
                System.out.println("Failed to load texture " + path);
                return null;
            }

            int format = switch (noc.get(0)) {
                case 3 -> GL_RGB;
                case 4 -> GL_RGBA;
                default -> GL_RED;
            };

            int id = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, id);
            glTexImage2D(GL_TEXTURE_2D, 0, format, w.get(0), h.get(0), 0, format, GL_UNSIGNED_BYTE, data);
            glGenerateMipmap(GL_TEXTURE_2D);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

            STBImage.stbi_image_free(data);

            Texture texture = new Texture(path, id, w.get(), h.get());
            loadedTextures.put(path, texture);

            return texture;
        }
    }

    public static void disposeTexture(Texture texture) {
        if (texture != null) {
            glDeleteTextures(texture.getId());
        }
    }

    public static Texture createTextureResized(Path path, int targetWidth, int targetHeight) {

        if (loadedTextures.containsKey(path)) {
            return loadedTextures.get(path);
        }


        ByteBuffer imageBuffer;
        int originalWidth, originalHeight, channels;

        // Load image with STBImage
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer widthBuffer = stack.mallocInt(1);
            IntBuffer heightBuffer = stack.mallocInt(1);
            IntBuffer channelsBuffer = stack.mallocInt(1);

            imageBuffer = STBImage.stbi_load(path.toString(), widthBuffer, heightBuffer, channelsBuffer, 4); // 4 = RGBA
            if (imageBuffer == null) {
                throw new RuntimeException("Failed to load image: " + STBImage.stbi_failure_reason());
            }

            originalWidth = widthBuffer.get(0);
            originalHeight = heightBuffer.get(0);
            channels = channelsBuffer.get(0);
        }

        // Resize the image data
        ByteBuffer resizedBuffer = resizeImage(imageBuffer, originalWidth, originalHeight, targetWidth, targetHeight, channels);
        STBImage.stbi_image_free(imageBuffer);  // Free original image buffer

        // Generate OpenGL texture
        int textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureID);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, targetWidth, targetHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, resizedBuffer);

        // Set texture parameters for scaling
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        MemoryUtil.memFree(resizedBuffer);  // Free resized buffer

        Texture texture = new Texture(path, textureID, targetWidth, targetHeight);
        loadedTextures.put(path, texture);

        return texture;
    }

    private static ByteBuffer resizeImage(ByteBuffer originalBuffer, int originalWidth, int originalHeight, int targetWidth, int targetHeight, int channels) {
        ByteBuffer resizedBuffer = MemoryUtil.memAlloc(targetWidth * targetHeight * channels);

        // Simple nearest-neighbor scaling for downsampling
        for (int y = 0; y < targetHeight; y++) {
            for (int x = 0; x < targetWidth; x++) {
                int srcX = x * originalWidth / targetWidth;
                int srcY = y * originalHeight / targetHeight;
                int srcIndex = (srcX + srcY * originalWidth) * channels;
                int destIndex = (x + y * targetWidth) * channels;

                for (int c = 0; c < channels; c++) {
                    resizedBuffer.put(destIndex + c, originalBuffer.get(srcIndex + c));
                }
            }
        }
        return resizedBuffer;
    }

}
