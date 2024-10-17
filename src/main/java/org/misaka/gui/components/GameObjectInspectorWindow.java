package org.misaka.gui.components;

import imgui.ImGui;
import imgui.type.ImBoolean;
import imgui.type.ImString;
import lombok.Data;
import lombok.SneakyThrows;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.misaka.core.Component;
import org.misaka.core.GameObject;
import org.misaka.core.components.CameraComponent;
import org.misaka.core.components.ScriptComponent;
import org.misaka.core.components.SpriteComponent;
import org.misaka.core.components.TransformComponent;
import org.misaka.engine.EngineEventManager;
import org.misaka.factory.ComponentFactory;
import org.misaka.gui.GameEngineUIComponent;
import org.misaka.managers.SceneManager;
import org.misaka.utils.Icons;
import org.misaka.utils.Utils;

import java.lang.ref.WeakReference;
import java.nio.file.Paths;
import java.util.*;

@Data
public class GameObjectInspectorWindow implements GameEngineUIComponent {

    private WeakReference<GameObject>  gameObject;
    private Map<String, Runnable> components;

    public GameObjectInspectorWindow() {
        components = new LinkedHashMap<>();
        components.put("Transform", this::renderTransformComponent);
        components.put("Sprite", this::renderSpriteComponent);
        components.put("Script", this::renderScriptComponent);
        components.put("Camera", this::renderCameraComponent);

        gameObject = new WeakReference<>(null);

        EngineEventManager.addListener("GameObjectDeleted", this::removeSelectedGameObjectEventCallback);
    }


    public void setGameObject(GameObject gameObject) {
        this.gameObject = new WeakReference<>(gameObject);
    }


    public GameObject getGameObject() {
        return this.gameObject.get();
    }

    /**
     * Callback function triggered when game object is deleted. If current selected game object is this deleted game
     * object or current selected object is child of deleted object then clear weak reference.
     * @param object Deleted game object.
     */
    public void removeSelectedGameObjectEventCallback(Object object) {
        GameObject gameObject = (GameObject) object;
        if ((this.gameObject.get() == gameObject) || (gameObject.isChild(this.gameObject.get()))) {
            this.gameObject.clear();
        }
    }

    @SneakyThrows
    @Override
    public void render() {
        ImGui.begin(Icons.CircleInfo + " Inspector");
        if (this.gameObject != null && this.gameObject.get() != null) {
            GameObject gameObject = this.gameObject.get();

            ImGui.columns(2);
            ImGui.setColumnWidth(-1, 100);
            ImGui.text("ID");
            ImGui.nextColumn();
            ImString id = new ImString(Objects.requireNonNull(gameObject).getId().toString());
            ImGui.beginDisabled();
            ImGui.setNextItemWidth(-1);
            ImGui.inputText("##ID", id);
            ImGui.endDisabled();
            ImGui.nextColumn();

            ImString name = new ImString(gameObject.getName(), 255);
            ImGui.text("Name");
            ImGui.nextColumn();
            ImGui.setNextItemWidth(-1);
            ImGui.inputText("##Name", name);
            gameObject.setName(name.toString());
            ImGui.nextColumn();
            ImBoolean active = new ImBoolean(gameObject.isActive());
            ImGui.text("Active");
            ImGui.nextColumn();
            ImGui.checkbox("##Active", active);

            gameObject.setActive(active.get());
            ImGui.columns(1);
            ImGui.separator();
            ImGui.text("Components");
            ImGui.sameLine();
            if (ImGui.button(Icons.Plus)) {
                ImGui.openPopup("Add Component Menu");
            }
            for (Component component : gameObject.getComponents().values()) {
                components.get(component.getComponentType()).run();
            }

            renderAddComponentContextMenu(gameObject);


        }
        ImGui.end();
    }

    private void renderTransformComponent() {
        TransformComponent transformComponent = gameObject.get().getComponent(TransformComponent.class);
        if (ImGui.collapsingHeader("Transform Component")) {
            Vector3f position = transformComponent.getPosition();
            float[] posBuffer = new float[] { position.x, position.y, position.z };
            ImGui.dragFloat3("Position", posBuffer, 1f);
            transformComponent.getPosition().set(posBuffer);

            float[] rotBuffer = new float[] { transformComponent.getRotation() };
            ImGui.dragFloat("Rotation", rotBuffer, 0.01f);
            transformComponent.setRotation(rotBuffer[0]);
            Vector3f scale = transformComponent.getScale();

            float[] scaleBuffer = new float[] { scale.x, scale.y, scale.z };
            ImGui.dragFloat3("Scale", scaleBuffer, 1f);
            transformComponent.getScale().set(scaleBuffer);

        }
    }

    private void renderScriptComponent() {
        ScriptComponent scriptComponent = gameObject.get().getComponent(ScriptComponent.class);
        if (ImGui.collapsingHeader("Script Component")) {
            ImGui.beginDisabled();
            ImString str = new ImString(scriptComponent.getFilePath());
            ImGui.inputText("File Path", str);
            ImGui.endDisabled();
            if (ImGui.button("Add Script")) {
                String path = Utils.openFileDialog();
                if (path != null) {
                    scriptComponent.addScriptFile(SceneManager.getActiveScene(), Paths.get(path));
                }
            }
        }
    }

    private void renderSpriteComponent() {
        SpriteComponent spriteComponent = gameObject.get().getComponent(SpriteComponent.class);
        if (ImGui.collapsingHeader("Sprite Component")) {
            ImGui.beginDisabled();
            ImString str = new ImString(spriteComponent.getFilePath());
            ImGui.inputText("File Path", str);
            ImGui.endDisabled();
            if (ImGui.button("Add Sprite")) {
                String path = Utils.openFileDialog();
                if (path != null) {
                    spriteComponent.setTexture(path);
                }
            }
        }
    }

    private void renderCameraComponent( ) {
        CameraComponent cameraComponent = gameObject.get().getComponent(CameraComponent.class);
        if (ImGui.collapsingHeader("Camera Component")) {
            Vector3f bg = cameraComponent.getBackground();
            float[] background = new float[] {
                    bg.x, bg.y, bg.z
            };
            ImGui.colorEdit3("Background", background);
            cameraComponent.setBackground(new Vector3f(background));

            float[] viewport = new float[] {
                    cameraComponent.getViewport().x,
                    cameraComponent.getViewport().y
            };
            ImGui.dragFloat2("Viewport", viewport);
            cameraComponent.setViewport(new Vector2f(viewport));
        }
    }

    private void renderAddComponentContextMenu(GameObject gameObject) {
        if (ImGui.beginPopup("Add Component Menu")) {
            for (String componentType : components.keySet()) {
                boolean hasComponent = gameObject.getComponent(Component.components.get(componentType)) != null;

                if (componentType.equals("Transform")) {
                    ImGui.beginDisabled();
                }
                if (ImGui.menuItem(componentType, hasComponent ? Icons.Check : null)) {
                    if (hasComponent) {
                        gameObject.removeComponent(componentType);
                    } else {
                        gameObject.addComponent(Objects.requireNonNull(ComponentFactory.createComponent(componentType)));
                    }
                }
                if (componentType.equals("Transform")) {
                    ImGui.endDisabled();
                }
            }
            ImGui.endPopup();
        }
    }
}
