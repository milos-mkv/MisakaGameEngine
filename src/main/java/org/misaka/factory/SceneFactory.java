package org.misaka.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.misaka.core.GameObject;
import org.misaka.core.Scene;

public abstract class SceneFactory {

    private static ObjectMapper objectMapper;

    /**
     * Initialise scene factory.
     */
    public static void init() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    /**
     * Create scene.
     * @param name Scene name.
     * @return Scene
     */
    public static Scene createScene(String name) {
        Scene scene = new Scene(name);
        addCustomScriptPath(scene);
        return scene;
    }

    /**
     * Add custom script path and init lua globals for scene.
     * @param scene Scene.
     */
    private static void addCustomScriptPath(Scene scene) {
        scene.setGlobals(JsePlatform.standardGlobals());
        scene.getGlobals().loadfile("./src/main/resources/scripts/classes.lua").call();
        String customScriptsPath = "./src/main/resources/scripts/";
        String existingPath = scene.getGlobals().get("package").get("path").tojstring();
        scene.getGlobals().get("package").set("path", LuaValue.valueOf(customScriptsPath + existingPath));
    }

    /**
     * Generate json from scene.
     * @param scene Scene.
     * @return String
     */
    public static String generateJsonFromScene(Scene scene) {
        String json = null;
        try {
            json = objectMapper.writeValueAsString(scene);
        } catch (JsonProcessingException e) {
            System.out.println(e);
        }
        return json;
    }

    /**
     * Generate scene from json.
     * @param json json string.
     * @return Scene
     */
    public static Scene generateSceneFromJson(String json) {
        try {
            final Scene scene = objectMapper.readValue(json, Scene.class);
            // We have to reassing all parent to game objects because we ignore parents in json.
            scene.getRootGameObject().getChildren().forEach((value) -> reassignParentGameObjects(scene.getRootGameObject()));
            // Also load script paths because we also don't save globals to json.
            addCustomScriptPath(scene);
            return scene;
        } catch (JsonProcessingException e) {
            System.out.println(e);
        }
        return null;
    }

    /**
     * Reassign parents to game objects.
     * @param gameObject Game Object.
     */
    private static void reassignParentGameObjects(GameObject gameObject) {
        for (GameObject child : gameObject.getChildren()) {
            child.setParent(gameObject);
            reassignParentGameObjects(child);
        }
    }
}
