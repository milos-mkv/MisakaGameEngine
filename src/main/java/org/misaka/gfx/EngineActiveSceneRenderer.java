package org.misaka.gfx;

import lombok.Getter;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.misaka.core.GameObject;
import org.misaka.core.Scene;
import org.misaka.core.Settings;
import org.misaka.core.components.CameraComponent;
import org.misaka.core.components.TransformComponent;
import org.misaka.factory.GameObjectFactory;
import org.misaka.managers.SceneManager;

import java.util.Set;

import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

public class EngineActiveSceneRenderer {

    @Getter
    private static final EngineActiveSceneRenderer instance = new EngineActiveSceneRenderer();

    private final FrameBuffer frameBuffer;
    private final GameObject camera;

    public EngineActiveSceneRenderer() {
        frameBuffer = new FrameBuffer(1920, 1280, false);
        camera = GameObjectFactory.createCameraGameObject("Engine UI Camera");

        TransformComponent transformComponent = camera.getComponent(TransformComponent.class);
        transformComponent.setPosition(new Vector3f(0, 0, 1));
        CameraComponent cameraComponent = camera.getComponent(CameraComponent.class);
        cameraComponent.setViewport(new Vector2f(1920, 1280));

    }

    public void render() {
        Scene scene = SceneManager.getActiveScene();
        if (scene == null) {
            return;
        }
        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer.getId());
        glViewport(0, 0, (int) Settings.w, (int) Settings.h);



        glBindFramebuffer(GL_FRAMEBUFFER, 0);

    }
}
