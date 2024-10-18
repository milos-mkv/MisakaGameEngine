package org.misaka.engine;

import lombok.Data;
import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.misaka.core.components.TransformComponent;

@Data
public class EngineCameraController {

    @Getter
    private static final EngineCameraController instance = new EngineCameraController();

    private Vector4f background;
    private Vector2f viewport;
    private TransformComponent transform;

    public EngineCameraController() {
        this.background = new Vector4f(0.05f, 0.05f, 0.05f, 1);
        this.viewport = new Vector2f();
        this.transform = new TransformComponent();
        this.transform.setPosition(new Vector3f(0, 0, 2));
    }

    public Matrix4f getProjectionMatrix() {
        return new Matrix4f().ortho(-viewport.x / 2, viewport.x / 2, -viewport.y / 2, viewport.y /2, -2.0f, 2.0f);
    }

    public Matrix4f getViewMatrix() {
        return this.transform.getTransformMatrix();
    }

    public void zoom(float delta) {
        var scale = transform.getScale();
        scale.x += delta;
        scale.y += delta;
    }

    public void move(float deltaX, float deltaY) {
        this.transform.getPosition().add(new Vector3f(
                deltaX, deltaY, 0
        ));
    }
}
