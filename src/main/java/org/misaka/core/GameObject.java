package org.misaka.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@JsonDeserialize
@NoArgsConstructor
@Data
public class GameObject {

    private UUID id;
    public String name;
    private List<GameObject> children;
    private HashMap<Class<?>, Component> components;
    private boolean active;
    @JsonIgnore
    @ToString.Exclude
    private GameObject parent;

    public GameObject(UUID id, String name) {
        this.id = id;
        this.name = name;
        this.children = new ArrayList<GameObject>();
        this.components = new HashMap<>();
        this.parent = null;
        this.active = true;
    }

    public void removeFromParent() {
        if (this.parent != null) {
            this.parent.removeChild(this);
        }
    }

    public void addChild(GameObject gameObject) {
        addChild(gameObject, this.children.size());
    }

    public void addChild(GameObject gameObject, int index) {
        if (gameObject == this) {
            return;
        }
        gameObject.removeFromParent();
        this.children.add(index, gameObject);
        gameObject.setParent(this);
    }

    public int getChildIndex(GameObject gameObject) {
        return children.indexOf(gameObject);
    }

    public void removeChild(GameObject gameObject) {
        this.children.remove(gameObject);
        if (gameObject != null) {
            gameObject.setParent(null);
        }
    }

    public void removeChild(int index) {
        try {
            GameObject gameObject = this.children.remove(index);
            gameObject.setParent(null);
        } catch (RuntimeException e) {
            System.out.println("Failed to remove game object from children!");
        }
    }

    public <T> void addComponent(T component) {
        this.components.put(component.getClass(), (Component) component);
    }

    @SuppressWarnings("unchecked")
    public <T> T getComponent(Class<T> componentType) {
        return (T) this.components.get(componentType);
    }

    @SuppressWarnings("unchecked")
    public <T> T getComponent(String componentType) {
        return (T) this.components.get(Component.components.get(componentType));
    }

    public GameObject find(String name) {
        for (GameObject child : children) {
            if (child.getName().equals(name)) {
                return child;
            }
            GameObject inChild = child.find(name);
            if (inChild != null) {
                return inChild;
            }
        }
        return null;
    }
}
