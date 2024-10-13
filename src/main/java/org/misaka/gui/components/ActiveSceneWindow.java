package org.misaka.gui.components;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiMouseButton;
import imgui.flag.ImGuiStyleVar;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.misaka.core.GameObject;
import org.misaka.core.Scene;
import org.misaka.core.Settings;
import org.misaka.core.components.CameraComponent;
import org.misaka.core.components.TransformComponent;
import org.misaka.factory.GameObjectFactory;
import org.misaka.gfx.FrameBuffer;
import org.misaka.gfx.Renderer;
import org.misaka.gui.GameEngineUIComponent;
import org.misaka.managers.SceneManager;

import java.util.Set;

public class ActiveSceneWindow implements GameEngineUIComponent {

    private ImVec2 mouseLastPosition;
    public ActiveSceneWindow() {
        mouseLastPosition = new ImVec2(0, 0);
    }

    @Override
    public void render() {
        Scene scene = SceneManager.getActiveScene();
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);
        ImGui.begin("Scene");
        Settings.w = ImGui.getWindowWidth();
        Settings.h = ImGui.getWindowHeight();
        float windowHeight = ImGui.getWindowSize().y;  // Total window height
        float titleBarHeight = ImGui.getFrameHeight(); // Title bar height
        float contentHeight = windowHeight - titleBarHeight;  // Height excluding the title bar

        if (scene != null) {
            ImGui.image(scene.getFrameBuffer().getTexture(),
                    ImGui.getWindowSizeX(), contentHeight, 0, 1, 1, 0);

        }

        ImVec2 currentMouseCursor = ImGui.getMousePos();

        if (ImGui.isWindowHovered() && ImGui.isMouseDragging(ImGuiMouseButton.Right)) {
            var transform = Renderer.getInstance().getEngineCamera().getComponent(TransformComponent.class);
            if (transform != null) {
                var pos = transform.getPosition();
                pos.x += currentMouseCursor.x - mouseLastPosition.x;
                pos.y -= currentMouseCursor.y - mouseLastPosition.y;

            }
        }

        if (ImGui.isWindowHovered() && ImGui.getIO().getMouseWheel() != 0.0f) {
            System.out.println(ImGui.getIO().getMouseWheel());
            var transform = Renderer.getInstance().getEngineCamera().getComponent(TransformComponent.class);
            var scale = transform.getScale();
            scale.x += ImGui.getIO().getMouseWheel() * 0.1;
            scale.y += ImGui.getIO().getMouseWheel()* 0.1;

        }
        mouseLastPosition = currentMouseCursor;
        ImGui.end();
        ImGui.popStyleVar();
    }
}
