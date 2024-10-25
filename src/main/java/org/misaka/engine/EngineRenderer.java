package org.misaka.engine;

import lombok.Getter;
import org.joml.Vector2f;
import org.misaka.core.Scene;
import org.misaka.core.Settings;
import org.misaka.gfx.Renderer;
import org.misaka.gfx.renderables.GridRenderer;
import org.misaka.gfx.renderables.LineAxisRenderer;
import org.misaka.managers.SceneManager;


import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

public class EngineRenderer {

    @Getter
    private static final EngineRenderer instance = new EngineRenderer();

    private GridRenderer gridRenderer;

    private EngineRenderer() {
        this.gridRenderer = new GridRenderer();
    }

    public void render(float delta) {
        Scene scene = SceneManager.getActiveScene();

        glBindFramebuffer(GL_FRAMEBUFFER, scene.getFrameBuffer().getId());
        glViewport(0, 0, scene.getFrameBuffer().getWidth(), scene.getFrameBuffer().getHeight());


        var cameraController = EngineCameraController.getInstance();
        cameraController.setViewport(new Vector2f(Settings.w, Settings.h));

        glClearColor(cameraController.getBackground().x, cameraController.getBackground().y, cameraController.getBackground().z, 1);
        glClear(GL_COLOR_BUFFER_BIT);

        gridRenderer.render();
        LineAxisRenderer.getInstance().render();
        Renderer.getInstance().render(true);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);

    }

    public void render1(float delta) {
        Scene scene = SceneManager.getActiveScene();

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, scene.getFrameBuffer().getWidth(), scene.getFrameBuffer().getHeight());


        var cameraController = EngineCameraController.getInstance();
        cameraController.setViewport(new Vector2f(800, 600));

        glClearColor(cameraController.getBackground().x, cameraController.getBackground().y, cameraController.getBackground().z, 1);
        glClear(GL_COLOR_BUFFER_BIT);
        glViewport(0, 0, 1000, 1000);

        gridRenderer.render();
        LineAxisRenderer.getInstance().render();
//        Renderer.getInstance().render(true);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);

    }
}
