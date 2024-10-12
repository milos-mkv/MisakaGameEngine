package org.misaka.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.luaj.vm2.Globals;
import org.misaka.factory.GameObjectFactory;
import org.misaka.gfx.FrameBuffer;


@Data
@JsonDeserialize
@NoArgsConstructor
public class Scene {

    private String name;
    private GameObject rootGameObject;
    @JsonIgnore
    private FrameBuffer frameBuffer;
    @JsonIgnore
    private Globals globals;

    /**
     * Constructor.
     * @param name Scene name.
     */
    public Scene(String name) {
        this.name = name;
        this.rootGameObject = GameObjectFactory.createGameObject("Root");
    }

    /**
     * Add game object to scene.
     * @param gameObject Game Object.
     */
    public void addGameObject(GameObject gameObject) {
        this.rootGameObject.addChild(gameObject);
    }

    public void addGameObject(GameObject gameObject, int index) {
        this.rootGameObject.addChild(gameObject, index);
    }

    /**
     * Remove game object from scene.
     * @param gameObject Game Object.
     */
    public void removeGameObject(GameObject gameObject) {
        this.rootGameObject.removeChild(gameObject);
    }

    public GameObject find(String name) {
        return rootGameObject.find(name);
    }
}
