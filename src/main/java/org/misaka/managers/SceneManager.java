package org.misaka.managers;

import lombok.Getter;
import lombok.Setter;
import org.misaka.core.Scene;

import java.util.HashMap;
import java.util.Map;

public abstract class SceneManager {

    @Getter
    private static Map<String, Scene> scenes;
    @Getter
    @Setter
    private static Scene activeScene;

    public static void init() {
        scenes = new HashMap<>();
        activeScene = null;
    }

    public static void addScene(Scene scene) {
        scenes.put(scene.getName(), scene);
    }
}
