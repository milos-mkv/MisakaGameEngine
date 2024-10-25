package org.misaka.gfx;

import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.misaka.core.GameObject;
import org.misaka.core.Scene;
import org.misaka.core.Settings;
import org.misaka.core.components.CameraComponent;
import org.misaka.core.components.SpriteComponent;
import org.misaka.core.components.TransformComponent;
import org.misaka.engine.EngineCameraController;
import org.misaka.factory.ShaderFactory;
import org.misaka.gfx.renderables.GridRenderer;
import org.misaka.gfx.renderables.LineAxisRenderer;
import org.misaka.managers.SceneManager;

import java.nio.file.Paths;

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
    private final LineRectangleMesh lineRectangleMesh;

    public Renderer() {
        this.spriteRenderer = SpriteRenderer.getInstance();
        lineRectangleMesh = new LineRectangleMesh();
        ShaderFactory.createShaderProgram("Default",
                Paths.get("./src/main/resources/shaders/shader.vert"),
                Paths.get("./src/main/resources/shaders/shader.frag")
        );

    }

    public void render(boolean t) {
        Scene scene = SceneManager.getActiveScene();

        ShaderProgram shaderProgram = ShaderFactory.getShaders().get("Default");
        shaderProgram.use();
        if (true) {
            shaderProgram.setUniformMatrix4("projection", EngineCameraController.getInstance().getProjectionMatrix());
            shaderProgram.setUniformMatrix4("view", EngineCameraController.getInstance().getViewMatrix().invert());
        } else{
            var camera = scene.getRootGameObject().find("Main Camera");
            shaderProgram.setUniformMatrix4("projection",camera.getComponent(CameraComponent.class).getCameraProjection());
            shaderProgram.setUniformMatrix4("view", camera.getComponent(TransformComponent.class).getTransformMatrix().invert());
        }
        renderGameObject(scene.getRootGameObject(), null);
    }

    // Call this method to render the grid
    private void renderGameObject(GameObject gameObject, Matrix4f parentMatrix) {
        SpriteComponent spriteComponent = gameObject.getComponent(SpriteComponent.class);
        TransformComponent transformComponent = gameObject.getComponent(TransformComponent.class);
        Matrix4f worldTransform = transformComponent.getTransformMatrix();

        if (spriteComponent != null && spriteComponent.getTexture() != null) {
            System.out.println(spriteComponent);

            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, spriteComponent.getTexture().getId());

            if (parentMatrix != null) {
                parentMatrix.mul(transformComponent.getTransformMatrix(), worldTransform);
            }
            ShaderFactory.getShaders()
                    .get("Default")
                    .setUniformMatrix4("model", worldTransform);

            ShaderFactory.getShaders()
                    .get("Default").setUniformBoolean("isCamera", 0);
            spriteRenderer.render();
        }

        CameraComponent cameraComponent = gameObject.getComponent(CameraComponent.class);
        if (cameraComponent != null) {
            renderCameraObject(gameObject, parentMatrix);
        }
        for (GameObject child : gameObject.getChildren()) {
            renderGameObject(child, worldTransform);

        }
    }

    private void renderCameraObject(GameObject gameObject, Matrix4f parentMatrix) {

        ShaderProgram shaderProgram = ShaderFactory.getShaders().get("Default");
        CameraComponent cameraComponent = gameObject.getComponent(CameraComponent.class);
        TransformComponent transformComponent = gameObject.getComponent(TransformComponent.class);
        shaderProgram.setUniformBoolean("isCamera", 1);

        transformComponent.setScale(new Vector3f(
                cameraComponent.getViewport(), 1.0f
        ));

        Matrix4f worldTransform = transformComponent.getTransformMatrix();

        if (parentMatrix != null) {
            parentMatrix.mul(transformComponent.getTransformMatrix(), worldTransform);
        }

        shaderProgram.setUniformMatrix4("model", worldTransform);

        shaderProgram.setUniformVec3("bgColor", cameraComponent.getBackground());
//        spriteRenderer.render();
        shaderProgram.setUniformVec3("bgColor", new Vector3f(1, 1, 1));

//        glEnable(GL_LINE_SMOOTH);
//
//        glEnable(GL_BLEND);
//        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        lineRectangleMesh.render();
//        glDisable(GL_LINE_SMOOTH);

        transformComponent.setScale(new Vector3f(1.0f, 1.0f, 1.0f));
    }

}
