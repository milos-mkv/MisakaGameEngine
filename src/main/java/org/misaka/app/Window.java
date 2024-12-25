package org.misaka.app;

import lombok.Data;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;
import org.misaka.utils.Disposable;

import java.util.Objects;

/**
 * Handles creation of GLFW type window. Used for creating Engine window same for Game window.
 */
@Data
public class Window implements Disposable {

    private long handle;

    /**
     * Constructor.
     * @param title Window title.
     * @param width Window width.
     * @param height Window height.
     */
    public Window(String title, int width, int height) throws RuntimeException {
        handle = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);
        if (handle == MemoryUtil.NULL) {
            throw new RuntimeException("Failed to create GLFW window!");
        }

        GLFWVidMode videoMode = Objects.requireNonNull(GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor()));
        GLFW.glfwSetWindowPos(handle, (videoMode.width() - width) / 2, (videoMode.height() - height) / 2);

        GLFW.glfwMakeContextCurrent(handle);
        GL.createCapabilities();
    }

    /**
     * Dispose of GLFW window handle.
     */
    @Override
    public void dispose() {
        GLFW.glfwDestroyWindow(handle);
    }
}
