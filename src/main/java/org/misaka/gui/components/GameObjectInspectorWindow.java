package org.misaka.gui.components;

import imgui.ImGui;
import imgui.type.ImFloat;
import imgui.type.ImString;
import lombok.Data;
import org.joml.Vector3f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.nfd.NativeFileDialog;
import org.misaka.core.GameObject;
import org.misaka.core.Scene;
import org.misaka.core.components.ScriptComponent;
import org.misaka.core.components.SpriteComponent;
import org.misaka.core.components.TransformComponent;
import org.misaka.gui.GameEngineUIComponent;
import org.misaka.managers.SceneManager;
import org.misaka.utils.Utils;

import java.nio.file.Paths;

@Data
public class GameObjectInspectorWindow implements GameEngineUIComponent {

    private GameObject gameObject;

    public GameObjectInspectorWindow() {
    }

    @Override
    public void render() {
        ImGui.begin("Inspector");
        if (gameObject != null) {
            ImGui.text(gameObject.getId().toString());
            ImGui.text(gameObject.getName());
            ImGui.separator();
            ImGui.text("Components");

            if (gameObject.getComponent(TransformComponent.class) != null) {
                TransformComponent transformComponent = gameObject.getComponent(TransformComponent.class);
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

            if (gameObject.getComponent(ScriptComponent.class) != null) {
                ScriptComponent scriptComponent = gameObject.getComponent(ScriptComponent.class);
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

            if (gameObject.getComponent(SpriteComponent.class) != null) {
                SpriteComponent spriteComponent = gameObject.getComponent(SpriteComponent.class);
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

        }
        ImGui.end();
    }
}
