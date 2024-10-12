package org.misaka.core.components;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.misaka.core.Component;

@Data
public class TransformComponent extends Component {

    private Vector3f position;
    private float rotation;
    private Vector3f scale;

    public TransformComponent() {
        super("Transform");
        position = new Vector3f(0, 0, 0);
        rotation = 0;
        scale = new Vector3f(1, 1, 1);
    }

    @JsonIgnore
    public Matrix4f getTransformMatrix() {
        return new Matrix4f().identity()
                .translate(position)
                .rotate(rotation, new Vector3f(0, 0, 1))
                .scale(scale);
    }
}
