package org.misaka.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.misaka.core.GameObject;
import org.misaka.core.components.TransformComponent;

import java.util.UUID;

public abstract class GameObjectFactory {

    private static ObjectMapper objectMapper;

    /**
     * Initialise game object factory.
     */
    public static void init() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Create game object.
     * @return GameObject
     */
    public static GameObject createGameObject() {
        return createGameObject("Game Object");
    }

    /**
     * Create game object.
     * @param name Game Object name.
     * @return GameObject
     */
    public static GameObject createGameObject(String name) {
        GameObject gameObject = new GameObject(UUID.randomUUID(), name);
        gameObject.addComponent(new TransformComponent());
        return gameObject;
    }

    /**
     * Generate json from game object.
     * @param gameObject Game Object.
     * @return String
     */
    public static String generateJsonFromGameObject(GameObject gameObject) {
        String json = "";
        try {
            json = objectMapper.writeValueAsString(gameObject);
        } catch (JsonProcessingException e) {
            System.out.println(e);
        }
        return json;
    }

    /**
     * Generate game object from json.
     * @param json Json string.
     * @return GameObject
     */
    public static GameObject generateGameObjectFromJson(String json) {
        GameObject gameObject = null;
        try {
            gameObject = objectMapper.readValue(json, GameObject.class);
        } catch (JsonProcessingException e) {
            System.out.println(e);
        }
        return gameObject;
    }

}
