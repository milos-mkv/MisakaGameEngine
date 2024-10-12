package org.misaka.gfx;

import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.misaka.Main;
import org.misaka.core.GameObject;
import org.misaka.core.Scene;
import org.misaka.core.components.CameraComponent;
import org.misaka.core.components.SpriteComponent;
import org.misaka.core.components.TransformComponent;
import org.misaka.factory.ShaderFactory;
import org.misaka.managers.SceneManager;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

public class Renderer {

    @Getter
    private static final Renderer instance = new Renderer();

    private final SpriteRenderer spriteRenderer;

    public Renderer() {
        this.spriteRenderer = SpriteRenderer.getInstance();
    }

    public void render() {
        Scene scene = SceneManager.getActiveScene();
        if (scene == null) {
            return;
        }
        glBindFramebuffer(GL_FRAMEBUFFER, scene.getFrameBuffer().getId());
        glViewport(0, 0, scene.getFrameBuffer().getWidth(), scene.getFrameBuffer().getHeight());
        glClearColor(0, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT);

        ShaderProgram shaderProgram = ShaderFactory.getShaders().get("default");
        shaderProgram.use();

        GameObject cameraGameObject = scene.find("Main Camera");

        TransformComponent transformComponent = cameraGameObject.getComponent(TransformComponent.class);
        if (transformComponent != null) {
            shaderProgram.setUniformMatrix4("view", transformComponent.getTransformMatrix());
        }

        CameraComponent cameraComponent = cameraGameObject.getComponent(CameraComponent.class);
        if (cameraComponent != null) {
            shaderProgram.setUniformMatrix4("projection", cameraComponent.getCameraProjection());
        }

        renderGameObject(scene.getRootGameObject(), null);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    private void renderGameObject(GameObject gameObject, Matrix4f parentMatrix) {
        SpriteComponent spriteComponent = gameObject.getComponent(SpriteComponent.class);
        TransformComponent transformComponent = gameObject.getComponent(TransformComponent.class);
        Matrix4f worldTransform = transformComponent.getTransformMatrix();

        if (spriteComponent != null && spriteComponent.getTexture() != null) {
            System.out.println(gameObject);
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, spriteComponent.getTexture().getId());

            if (parentMatrix != null) {
                parentMatrix.mul(transformComponent.getTransformMatrix(), worldTransform);
            }
            ShaderFactory.getShaders()
                    .get("default")
                    .setUniformMatrix4("model", worldTransform);
            spriteRenderer.render();
        }
        for (GameObject child : gameObject.getChildren()) {
            renderGameObject(child, worldTransform);
        }
    }

}
