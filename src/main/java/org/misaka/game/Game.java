package org.misaka.game;

import lombok.Data;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;
import org.misaka.engine.EngineRenderer;
import org.misaka.factory.ShaderFactory;
import org.misaka.gfx.renderables.GridRenderer;
import org.misaka.managers.SceneManager;

import java.nio.file.Paths;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

@Data
public class Game {

    private GameConfiguration gameConfiguration;
    private long windowHandle = MemoryUtil.NULL;

    GridRenderer gridRenderer;

    public Game(GameConfiguration gameConfiguration) {
        this.gameConfiguration = gameConfiguration;
        createWindow();
        gridRenderer = new GridRenderer();
    }

    public void createWindow() {
        windowHandle = GLFW.glfwCreateWindow(gameConfiguration.windowWidth, gameConfiguration.windowHeight,
                gameConfiguration.windowTitle, MemoryUtil.NULL, MemoryUtil.NULL);
        if (windowHandle == MemoryUtil.NULL) {
            return;
        }
        final GLFWVidMode videoMode = Objects.requireNonNull(GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor()));
        GLFW.glfwSetWindowPos(windowHandle, (videoMode.width() - 1000) / 2, (videoMode.height() - 800) / 2);
//        GLFW.glfwMakeContextCurrent(windowHandle);
//        GL.createCapabilities();
        GLFW.glfwShowWindow(windowHandle);
        GLFW.glfwMakeContextCurrent(windowHandle);
        ShaderFactory.createShaderProgram("Grid1",
                Paths.get("./src/main/resources/shaders/grid.vert"),
                Paths.get("./src/main/resources/shaders/grid.frag")
        );
    }


    public void run() {
        if (windowHandle == MemoryUtil.NULL) {
            return;
        }
        if (GLFW.glfwWindowShouldClose(windowHandle)) {
            dispose();
            return;
        }
        GLFW.glfwMakeContextCurrent(windowHandle);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        glViewport(0, 0, 800, 600);
        glClearColor(1, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT);

        gridRenderer.render1();

        GLFW.glfwSwapBuffers(windowHandle);

    }

    public void dispose() {
        GLFW.glfwDestroyWindow(windowHandle);
        windowHandle = MemoryUtil.NULL;
    }

}
