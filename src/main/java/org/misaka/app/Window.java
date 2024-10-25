package org.misaka.app;

import lombok.Data;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;

import java.util.Objects;

@Data
public class Window {

    private long handle;

    public Window(String title, int width, int height) {
        handle = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);
        if (handle == MemoryUtil.NULL) {
            return;
        }
        GLFWVidMode videoMode = Objects.requireNonNull(GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor()));
        GLFW.glfwSetWindowPos(handle, (videoMode.width() - width) / 2, (videoMode.height() - height) / 2);

        GLFW.glfwMakeContextCurrent(handle);
        GL.createCapabilities();
    }

    public void dispose() {
        GLFW.glfwDestroyWindow(handle);
    }
}
