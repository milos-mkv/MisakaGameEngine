package org.misaka.gui;

import lombok.Data;
import lombok.Getter;
import org.misaka.gui.components.*;

import java.nio.file.Path;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class GameEngineUI {

    @Getter
    public static final GameEngineUI instance = new GameEngineUI();

    private Map<Class<?>, GameEngineUIComponent> components;

    private Map<Path, CodeEditorWindow> codeEditors;

    public GameEngineUI() {
        components = new LinkedHashMap<>();
        codeEditors = new LinkedHashMap<>();

        components.put(SceneHierarchyWindow.class, new SceneHierarchyWindow());
        components.put(GameObjectInspectorWindow.class, new GameObjectInspectorWindow());
        components.put(ActiveSceneWindow.class, new ActiveSceneWindow());
        components.put(MainMenuBar.class, new MainMenuBar());
        components.put(ConsoleWindow.class, new ConsoleWindow());
        components.put(ProjectWindow.class, new ProjectWindow());
    }

    public void render() {
        this.components.forEach((_, component) -> component.render());
        this.codeEditors.forEach((_, editor) -> editor.render());
    }

    public void addCodeEditor(Path path) {
        this.codeEditors.put(path, new CodeEditorWindow(path));
    }

    @SuppressWarnings("unchecked")
    public <T> T getComponent(Class<T> component) {
        return (T) components.get(component);
    }
}
