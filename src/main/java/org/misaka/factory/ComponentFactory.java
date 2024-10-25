package org.misaka.factory;

import org.misaka.core.Component;
import org.misaka.core.components.CameraComponent;
import org.misaka.core.components.ScriptComponent;
import org.misaka.core.components.SpriteComponent;
import org.misaka.core.components.TransformComponent;

public abstract class ComponentFactory {

    public static Component createComponent(String type) {
        return switch (type) {
            case "Transform" -> new TransformComponent();
            case "Sprite" -> new SpriteComponent();
            case "Script" -> new ScriptComponent();
            case "Camera" -> new CameraComponent();
            default -> null;
        };
    }
}
