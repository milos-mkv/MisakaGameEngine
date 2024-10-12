package org.misaka.gui;

import lombok.Data;
import lombok.Getter;
import org.misaka.gui.components.ActiveSceneWindow;
import org.misaka.gui.components.GameObjectInspectorWindow;
import org.misaka.gui.components.SceneHierarchyWindow;

import java.util.HashMap;
import java.util.Map;

@Data
public class GameEngineUI {

    @Getter
    public static final GameEngineUI instance = new GameEngineUI();

    private Map<Class<?>, GameEngineUIComponent> components;

    public GameEngineUI() {
        components = new HashMap<>();
        components.put(SceneHierarchyWindow.class, new SceneHierarchyWindow());
        components.put(GameObjectInspectorWindow.class, new GameObjectInspectorWindow());
        components.put(ActiveSceneWindow.class, new ActiveSceneWindow());
    }

    public void render() {
        this.components.forEach((_, component) -> component.render());
    }

    @SuppressWarnings("unchecked")
    public <T> T getComponent(Class<T> component) {
        return (T) components.get(component);
    }
}
