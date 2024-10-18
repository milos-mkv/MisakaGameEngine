package org.misaka.gfx;

import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.misaka.core.GameObject;
import org.misaka.core.Scene;
import org.misaka.core.Settings;
import org.misaka.core.components.CameraComponent;
import org.misaka.core.components.SpriteComponent;
import org.misaka.core.components.TransformComponent;
import org.misaka.engine.EngineCameraController;
import org.misaka.factory.ShaderFactory;
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

//    @Getter
//    private final GameObject engineCamera;
    LineRectangleMesh lineRectangleMesh;
    LineAxis lineAxis;
    Grid grid;
    public Renderer() {
        this.spriteRenderer = SpriteRenderer.getInstance();
//        this.engineCamera = GameObjectFactory.createCameraGameObject("Engine Camera");
//        this.engineCamera.getComponent(TransformComponent.class).setPosition(new Vector3f(0, 0, 1));
        lineRectangleMesh = new LineRectangleMesh();
        lineAxis = new LineAxis();
        grid = new Grid();
        ShaderFactory.createShaderProgram("grid",
                Paths.get("./src/main/resources/shaders/grid.vert"),
                Paths.get("./src/main/resources/shaders/grid.frag"));
    }

    public void render() {
        Scene scene = SceneManager.getActiveScene();
        if (scene == null) {
            return;
        }
        glBindFramebuffer(GL_FRAMEBUFFER, scene.getFrameBuffer().getId());
        glViewport(0, 0, scene.getFrameBuffer().getWidth(), scene.getFrameBuffer().getHeight());


        ShaderProgram axisShader =  ShaderFactory.getShaders().get("axis");
        ShaderProgram gridShader =  ShaderFactory.getShaders().get("grid");

        ShaderProgram shaderProgram = ShaderFactory.getShaders().get("default");
        shaderProgram.use();

        if (Settings.useCamera) {
            EngineCameraController engineCamera = EngineCameraController.getInstance();
            shaderProgram.setUniformMatrix4("view", engineCamera.getViewMatrix().invert());
            engineCamera.setViewport(new Vector2f(Settings.w, Settings.h));
            shaderProgram.setUniformMatrix4("projection", engineCamera.getProjectionMatrix());
            glClearColor(engineCamera.getBackground().x, engineCamera.getBackground().y, engineCamera.getBackground().z, 1);
            glClear(GL_COLOR_BUFFER_BIT);

            axisShader.use();
            axisShader.setUniformMatrix4("projection", engineCamera.getProjectionMatrix());
            axisShader.setUniformMatrix4("view", engineCamera.getViewMatrix().invert());

            lineAxis.render();
        } else {
            GameObject camera = scene.find("Main Camera");

            shaderProgram.setUniformMatrix4("view", camera.getComponent(TransformComponent.class).getTransformMatrix().invert());
//            engineCamera.setViewport(new Vector2f(Settings.w, Settings.h));
            shaderProgram.setUniformMatrix4("projection", camera.getComponent(CameraComponent.class).getCameraProjection());
            glClearColor(camera.getComponent(CameraComponent.class).getBackground().x, camera.getComponent(CameraComponent.class).getBackground().y, camera.getComponent(CameraComponent.class).getBackground().z, 1);
            glClear(GL_COLOR_BUFFER_BIT);

            axisShader.use();
            axisShader.setUniformMatrix4("projection",  camera.getComponent(CameraComponent.class).getCameraProjection());
            axisShader.setUniformMatrix4("view", camera.getComponent(TransformComponent.class).getTransformMatrix().invert());

            lineAxis.render();
        }

        TransformComponent t = new TransformComponent();
        t.setScale(new Vector3f(3000, 3000, 1));
        t.setPosition(EngineCameraController.getInstance().getTransform().getPosition());
        gridShader.use();
        gridShader.setUniformVec3("size",new Vector3f(
                Settings.w, Settings.h, 0

        ));
        gridShader.setUniformVec3("ogSize",new Vector3f(
                scene.getFrameBuffer().getWidth(), scene.getFrameBuffer().getHeight(), 0

        ));
        gridShader.setUniformMatrix4("model",t.getTransformMatrix());

        gridShader.setUniformFloat("zoom", EngineCameraController.getInstance().getTransform().getScale().x);
        gridShader.setUniformMatrix4("projection", EngineCameraController.getInstance().getProjectionMatrix());
        gridShader.setUniformMatrix4("view", EngineCameraController.getInstance().getViewMatrix().invert());

        gridShader.setUniformVec3("camPos", EngineCameraController.getInstance().getTransform().getPosition());
        grid.render();





        shaderProgram.use();

        renderGameObject(scene.getRootGameObject(), null);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    // Call this method to render the grid
    private void renderGameObject(GameObject gameObject, Matrix4f parentMatrix) {
        ShaderProgram shaderProgram =ShaderFactory.getShaders().get("default");

        SpriteComponent spriteComponent = gameObject.getComponent(SpriteComponent.class);
        TransformComponent transformComponent = gameObject.getComponent(TransformComponent.class);
        Matrix4f worldTransform = transformComponent.getTransformMatrix();

        if (spriteComponent != null && spriteComponent.getTexture() != null) {
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, spriteComponent.getTexture().getId());

            if (parentMatrix != null) {
                parentMatrix.mul(transformComponent.getTransformMatrix(), worldTransform);
            }
            ShaderFactory.getShaders()
                    .get("default")
                    .setUniformMatrix4("model", worldTransform);

            ShaderFactory.getShaders()
                    .get("default").setUniformBoolean("isCamera", 0);
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
        ShaderProgram shaderProgram = ShaderFactory.getShaders().get("default");
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

        glEnable(GL_LINE_SMOOTH);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        lineRectangleMesh.render();
        glDisable(GL_LINE_SMOOTH);

        transformComponent.setScale(new Vector3f(1.0f, 1.0f, 1.0f));
    }

}
