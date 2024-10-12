package org.misaka.factory;

import lombok.Getter;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
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

}
