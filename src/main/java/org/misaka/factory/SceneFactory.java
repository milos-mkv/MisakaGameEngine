package org.misaka.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.misaka.core.GameObject;
import org.misaka.core.Scene;
import org.misaka.core.components.CameraComponent;
import org.misaka.core.components.TransformComponent;
import org.misaka.gfx.FrameBuffer;

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
        scene.setFrameBuffer(new FrameBuffer(800, 600, false));

        GameObject mainCameraGameObject = GameObjectFactory.createGameObject("Main Camera");
        mainCameraGameObject.addComponent(new CameraComponent());

        mainCameraGameObject.getComponent(TransformComponent.class).setPosition(new Vector3f(0, 0, -1));
        mainCameraGameObject.getComponent(CameraComponent.class).setViewport(new Vector2f(800, 600));
        scene.addGameObject(mainCameraGameObject);
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
            GameObjectFactory.reconstructGameObject(scene, scene.getRootGameObject());
            addCustomScriptPath(scene);
            scene.setFrameBuffer(new FrameBuffer(800, 600, false));
            return scene;
        } catch (JsonProcessingException e) {
            System.out.println(e);
        }
        return null;
    }

    public static void disposeScene(Scene scene) {
        scene.getFrameBuffer().dispose();
    }
}
