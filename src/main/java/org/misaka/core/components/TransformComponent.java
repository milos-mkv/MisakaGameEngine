package org.misaka.core.components;

import lombok.Data;
import org.misaka.core.Component;

@Data
public class TransformComponent extends Component {
    private float x;
    private float y;
    private float z;

    public TransformComponent() {
        super("Transform");
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }
}
