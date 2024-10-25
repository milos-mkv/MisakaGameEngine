package org.misaka.gui;

import lombok.Data;
import lombok.Getter;
import org.misaka.gui.components.*;

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
        components.put(MainMenuBar.class, new MainMenuBar());
        components.put(ConsoleWindow.class, new ConsoleWindow());

        components.put(ProjectWindow.class, new ProjectWindow());
    }

    public void render() {
        this.components.forEach((_, component) -> component.render());
    }

    @SuppressWarnings("unchecked")
    public <T> T getComponent(Class<T> component) {
        return (T) components.get(component);
    }
}
