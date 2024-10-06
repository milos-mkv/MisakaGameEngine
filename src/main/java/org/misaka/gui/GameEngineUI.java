package org.misaka.gui;

import lombok.Data;
import lombok.Getter;
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
    }

    public void render() {
        this.components.forEach((_, component) -> component.render());
    }
}
