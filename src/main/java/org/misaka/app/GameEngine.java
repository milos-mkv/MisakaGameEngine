package org.misaka.app;

import imgui.ImGuiStyle;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.internal.ImGui;
import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;
import org.misaka.core.GameObject;
import org.misaka.core.Scene;
import org.misaka.core.components.ScriptComponent;
import org.misaka.core.components.SpriteComponent;
import org.misaka.core.components.TransformComponent;
import org.misaka.factory.GameObjectFactory;
import org.misaka.factory.SceneFactory;
import org.misaka.factory.ShaderFactory;
import org.misaka.factory.TextureFactory;
import org.misaka.gfx.Renderer;
import org.misaka.gfx.ShaderProgram;
import org.misaka.gfx.SpriteRenderer;
import org.misaka.gfx.Texture;
import org.misaka.gui.GameEngineUI;
import org.misaka.managers.SceneManager;

import java.nio.file.Paths;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

public class GameEngine {

    @Getter
    private static final GameEngine instance = new GameEngine();

    private long windowHandle;
    private ImGuiImplGlfw imGuiImplGlfw;
    private ImGuiImplGl3 imGuiImplGl3;

    public GameEngine() {
        initializeGlfw();
        initializeImGui();
    }

    private void initializeGlfw() {
        GLFWErrorCallback.createPrint(System.err).free();

        if (!GLFW.glfwInit()) {
            return;
        }
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, 4);
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);
        }
        windowHandle = GLFW.glfwCreateWindow(1000, 800, "Game Maker Studio", MemoryUtil.NULL, MemoryUtil.NULL);
        if (windowHandle == MemoryUtil.NULL) {
            return;
        }
        final GLFWVidMode videoMode = Objects.requireNonNull(GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor()));
        GLFW.glfwSetWindowPos(windowHandle, (videoMode.width() - 1000) / 2, (videoMode.height() - 800) / 2);

        GLFW.glfwMakeContextCurrent(windowHandle);
        GLFW.glfwShowWindow(windowHandle);

        GL.createCapabilities();
        glViewport(0, 0, 1000, 800);

    }

    private void initializeImGui() {
        ImGui.createContext();
        ImGui.styleColorsDark();
        ImGui.getIO().addConfigFlags(ImGuiConfigFlags.DockingEnable);// | ImGuiConfigFlags.ViewportsEnable);

        imGuiImplGlfw = new ImGuiImplGlfw();
        imGuiImplGl3 = new ImGuiImplGl3();
        imGuiImplGlfw.init(windowHandle, true);
        imGuiImplGl3.init("#version 130");
        setupStyle();
    }

    public void run() {
        GameEngineUI gameEngineUI = GameEngineUI.getInstance();

        GameObject ggg = null;
        Scene scene = SceneFactory.createScene("Main Scene");
        for (int i = 0; i < 10; i++) {
            GameObject g = GameObjectFactory.createGameObject("GameObject" + i);
            scene.addGameObject(g);
            if (i==0) {
                ggg = g;
                g.getComponent(TransformComponent.class).setScale(new Vector3f(100, 100, 1));
            }
            g.addComponent(new ScriptComponent());
            g.addComponent(new SpriteComponent());
//            g.getComponent(SpriteComponent.class).setTexture(
//                    Paths.get("./src/main/resources/images/misaka.png")
//            );
            for (int j = 0; j < 4; j++) {
                g.addChild(GameObjectFactory.createGameObject("GameObjectChild" + i + "_" + j));
            }
        }
        SceneManager.init();
        SceneManager.addScene(scene);
        SceneManager.setActiveScene(scene);
        GameObjectFactory.init();
        SceneFactory.init();

        Texture texture = TextureFactory.createTexture(Paths.get("./src/main/resources/images/misaka.png"));

        ShaderProgram shaderProgram = ShaderFactory.createShaderProgram("default",
                Paths.get("./src/main/resources/shaders/shader.vert"),
                Paths.get("./src/main/resources/shaders/shader.frag"));

        SpriteRenderer spriteRenderer = SpriteRenderer.getInstance();

        String a = SceneFactory.generateJsonFromScene(scene);
        Renderer renderer = Renderer.getInstance();
//        Matrix4f camProj = new Matrix4f().ortho(0, 800, 0, 600, -1.0f, 1.0f);
//        Matrix4f view = new Matrix4f();
//        view.translate(new Vector3f(0, 0, -1.0f));
//        view.rotate( 10, new Vector3f(0, 0, 1));
//        view.scale(new Vector3f(2, 1, 1));
        while (!GLFW.glfwWindowShouldClose(windowHandle)) {
            GLFW.glfwPollEvents();
//            glBindFramebuffer(GL_FRAMEBUFFER, scene.getFrameBuffer().getId());
//            glViewport(0, 0, 800, 600);

//            glClearColor(1, 0, 0, 1);
//            glClear(GL_COLOR_BUFFER_BIT);
//            glActiveTexture(GL_TEXTURE0); // activate the texture unit first before binding texture
//            glBindTexture(GL_TEXTURE_2D, texture.getId());
//            glUseProgram(shaderProgram.getId());
//            shaderProgram.setUniformMatrix4("projection", camProj);
//            shaderProgram.setUniformMatrix4("view", view);
//            if (ggg !=  null)
//                shaderProgram.setUniformMatrix4("model", ggg.getComponent(TransformComponent.class).getTransformMatrix());
//            int textureUniformLocation = glGetUniformLocation(shaderProgram.getId(), "uTexture");
//            glUniform1i(textureUniformLocation, 0);  // 0 corresponds to GL_TEXTURE0
//
//            spriteRenderer.render();
//            glBindTexture(GL_TEXTURE_2D, 0);
//            glBindFramebuffer(GL_FRAMEBUFFER, 0);
            renderer.render();
            imGuiImplGlfw.newFrame();
            ImGui.newFrame();
            ImGui.dockSpaceOverViewport(ImGui.getMainViewport());
            gameEngineUI.render();
            ImGui.showDemoWindow();

            ImGui.render();
            imGuiImplGl3.renderDrawData(ImGui.getDrawData());


            GLFW.glfwSwapBuffers(windowHandle);
        }
    }

    public void dispose() {
        imGuiImplGl3.dispose();
        imGuiImplGlfw.dispose();

        ImGui.destroyContext();
        GLFW.glfwDestroyWindow(windowHandle);
        GLFW.glfwTerminate();
    }
    private void setupStyle() {
        float[][] colors = ImGui.getStyle().getColors();

        colors[ImGuiCol.Text] = new float[]{1.00f, 1.00f, 1.00f, 1.00f};
        colors[ImGuiCol.TextDisabled] = new float[]{0.50f, 0.50f, 0.50f, 1.00f};
        colors[ImGuiCol.WindowBg] = new float[]{0.10f, 0.10f, 0.10f, 1.00f};
        colors[ImGuiCol.ChildBg] = new float[]{0.00f, 0.00f, 0.00f, 0.00f};
        colors[ImGuiCol.PopupBg] = new float[]{0.19f, 0.19f, 0.19f, 0.92f};
        colors[ImGuiCol.Border] = new float[]{0.19f, 0.19f, 0.19f, 0.29f};
        colors[ImGuiCol.BorderShadow] = new float[]{0.00f, 0.00f, 0.00f, 0.24f};
        colors[ImGuiCol.FrameBg] = new float[]{0.05f, 0.05f, 0.05f, 0.54f};
        colors[ImGuiCol.FrameBgHovered] = new float[]{0.19f, 0.19f, 0.19f, 0.54f};
        colors[ImGuiCol.FrameBgActive] = new float[]{0.20f, 0.22f, 0.23f, 1.00f};
        colors[ImGuiCol.TitleBg] = new float[]{0.00f, 0.00f, 0.00f, 1.00f};
        colors[ImGuiCol.TitleBgActive] = new float[]{0.06f, 0.06f, 0.06f, 1.00f};
        colors[ImGuiCol.TitleBgCollapsed] = new float[]{0.00f, 0.00f, 0.00f, 1.00f};
        colors[ImGuiCol.MenuBarBg] = new float[]{0.14f, 0.14f, 0.14f, 1.00f};
        colors[ImGuiCol.ScrollbarBg] = new float[]{0.05f, 0.05f, 0.05f, 0.54f};
        colors[ImGuiCol.ScrollbarGrab] = new float[]{0.34f, 0.34f, 0.34f, 0.54f};
        colors[ImGuiCol.ScrollbarGrabHovered] = new float[]{0.40f, 0.40f, 0.40f, 0.54f};
        colors[ImGuiCol.ScrollbarGrabActive] = new float[]{0.56f, 0.56f, 0.56f, 0.54f};
        colors[ImGuiCol.CheckMark] = new float[]{0.33f, 0.67f, 0.86f, 1.00f};
        colors[ImGuiCol.SliderGrab] = new float[]{0.34f, 0.34f, 0.34f, 0.54f};
        colors[ImGuiCol.SliderGrabActive] = new float[]{0.56f, 0.56f, 0.56f, 0.54f};
        colors[ImGuiCol.Button] = new float[]{0.05f, 0.05f, 0.05f, 0.54f};
        colors[ImGuiCol.ButtonHovered] = new float[]{0.19f, 0.19f, 0.19f, 0.54f};
        colors[ImGuiCol.ButtonActive] = new float[]{0.20f, 0.22f, 0.23f, 1.00f};
        colors[ImGuiCol.Header] = new float[]{0.00f, 0.00f, 0.00f, 0.52f};
        colors[ImGuiCol.HeaderHovered] = new float[]{0.00f, 0.00f, 0.00f, 0.36f};
        colors[ImGuiCol.HeaderActive] = new float[]{0.20f, 0.22f, 0.23f, 0.33f};
        colors[ImGuiCol.Separator] = new float[]{0.28f, 0.28f, 0.28f, 0.29f};
        colors[ImGuiCol.SeparatorHovered] = new float[]{0.44f, 0.44f, 0.44f, 0.29f};
        colors[ImGuiCol.SeparatorActive] = new float[]{0.40f, 0.44f, 0.47f, 1.00f};
        colors[ImGuiCol.ResizeGrip] = new float[]{0.28f, 0.28f, 0.28f, 0.29f};
        colors[ImGuiCol.ResizeGripHovered] = new float[]{0.44f, 0.44f, 0.44f, 0.29f};
        colors[ImGuiCol.ResizeGripActive] = new float[]{0.40f, 0.44f, 0.47f, 1.00f};
        colors[ImGuiCol.Tab] = new float[]{0.00f, 0.00f, 0.00f, 0.52f};
        colors[ImGuiCol.TabHovered] = new float[]{0.14f, 0.14f, 0.14f, 1.00f};
        colors[ImGuiCol.TabActive] = new float[]{0.20f, 0.20f, 0.20f, 0.36f};
        colors[ImGuiCol.TabUnfocused] = new float[]{0.00f, 0.00f, 0.00f, 0.52f};
        colors[ImGuiCol.TabUnfocusedActive] = new float[]{0.14f, 0.14f, 0.14f, 1.00f};
        colors[ImGuiCol.DockingPreview] = new float[]{0.33f, 0.67f, 0.86f, 1.00f};
        colors[ImGuiCol.DockingEmptyBg] = new float[]{0.00f, 0.00f, 0.00f, 1.00f};
        colors[ImGuiCol.PlotLines] = new float[]{1.00f, 0.00f, 0.00f, 1.00f};
        colors[ImGuiCol.PlotLinesHovered] = new float[]{1.00f, 0.00f, 0.00f, 1.00f};
        colors[ImGuiCol.PlotHistogram] = new float[]{1.00f, 0.00f, 0.00f, 1.00f};
        colors[ImGuiCol.PlotHistogramHovered] = new float[]{1.00f, 0.00f, 0.00f, 1.00f};
        colors[ImGuiCol.TableHeaderBg] = new float[]{0.00f, 0.00f, 0.00f, 0.52f};
        colors[ImGuiCol.TableBorderStrong] = new float[]{0.00f, 0.00f, 0.00f, 0.52f};
        colors[ImGuiCol.TableBorderLight] = new float[]{0.28f, 0.28f, 0.28f, 0.29f};
        colors[ImGuiCol.TableRowBg] = new float[]{0.00f, 0.00f, 0.00f, 0.00f};
        colors[ImGuiCol.TableRowBgAlt] = new float[]{1.00f, 1.00f, 1.00f, 0.06f};
        colors[ImGuiCol.TextSelectedBg] = new float[]{0.20f, 0.22f, 0.23f, 1.00f};
        colors[ImGuiCol.DragDropTarget] = new float[]{0.33f, 0.67f, 0.86f, 1.00f};
        colors[ImGuiCol.NavHighlight] = new float[]{0.00f, 0.00f, 0.00f, 1.00f};
        colors[ImGuiCol.NavWindowingHighlight] = new float[]{0.00f, 0.00f, 0.00f, 0.70f};
        colors[ImGuiCol.NavWindowingDimBg] = new float[]{0.00f, 0.00f, 0.00f, 0.20f};
        colors[ImGuiCol.ModalWindowDimBg] = new float[]{0.00f, 0.00f, 0.00f, 0.35f};

        ImGui.getStyle().setColors(colors);

        ImGuiStyle style = ImGui.getStyle();
        style.setWindowPadding(8.00f, 8.00f);
        style.setFramePadding(5.00f, 2.00f);
        style.setCellPadding(6.00f, 6.00f);
        style.setItemSpacing(6.00f, 6.00f);
        style.setItemInnerSpacing(6.00f, 6.00f);
        style.setTouchExtraPadding(0.00f, 0.00f);
        style.setIndentSpacing(25.0f);
        style.setScrollbarSize(15.0f);
        style.setGrabMinSize(10.0f);
        style.setWindowBorderSize(1.0f);
        style.setChildBorderSize(1.0f);
        style.setPopupBorderSize(1.0f);
        style.setFrameBorderSize(1.0f);
        style.setTabBorderSize(1.0f);
        style.setWindowRounding(7.0f);
        style.setChildRounding(4.0f);
        style.setFrameRounding(3.0f);
        style.setPopupRounding(4.0f);
        style.setScrollbarRounding(9.0f);
        style.setGrabRounding(3.0f);
        style.setLogSliderDeadzone(4.0f);
        style.setTabRounding(4.0f);
    }

}
