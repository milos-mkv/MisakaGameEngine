package org.misaka.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.misaka.core.components.CameraComponent;
import org.misaka.core.components.ScriptComponent;
import org.misaka.core.components.SpriteComponent;
import org.misaka.core.components.TransformComponent;

import java.util.HashMap;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TransformComponent.class, name = "TransformComponent"),
        @JsonSubTypes.Type(value = ScriptComponent.class, name = "ScriptComponent"),
        @JsonSubTypes.Type(value = SpriteComponent.class, name = "SpriteComponent"),
        @JsonSubTypes.Type(value = CameraComponent.class, name = "CameraComponent")

})
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Component {

    @JsonIgnore
    public static HashMap<String, Class<?>> components = new HashMap<>() {
        {
            put("Transform", TransformComponent.class);
            put("Script", ScriptComponent.class);
            put("Sprite", SpriteComponent.class);
            put("Camera", CameraComponent.class);
        }
    };

    private String componentType;
}
