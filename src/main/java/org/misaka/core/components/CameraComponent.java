package org.misaka.core.components;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.misaka.core.Component;

@Data
public class CameraComponent extends Component {

    private Vector2f viewport;
    private Vector3f background;

    public CameraComponent() {
        super("Camera");
        this.viewport = new Vector2f(0, 0);
        this.background = new Vector3f(0, 0, 0);
    }

    @JsonIgnore
    public Matrix4f getCameraProjection() {
        return new Matrix4f().ortho(0, viewport.x, 0, viewport.y, -1.0f, 1.0f);
    }

}
