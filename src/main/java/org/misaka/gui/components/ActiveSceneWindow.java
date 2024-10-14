package org.misaka.gui.components;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiMouseButton;
import imgui.flag.ImGuiStyleVar;
import org.joml.Vector3f;
import org.misaka.core.Scene;
import org.misaka.core.Settings;
import org.misaka.engine.EngineCameraController;
import org.misaka.gui.GameEngineUIComponent;
import org.misaka.managers.SceneManager;

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
            EngineCameraController.getInstance()
                    .move( mouseLastPosition.x -currentMouseCursor.x, currentMouseCursor.y -  mouseLastPosition.y);
        }

        if (ImGui.isWindowHovered() && ImGui.getIO().getMouseWheel() != 0.0f) {
            EngineCameraController.getInstance()
                    .zoom((float) (ImGui.getIO().getMouseWheel() * 0.05));
        }

        mouseLastPosition = currentMouseCursor;

        ImGui.end();
        ImGui.popStyleVar();
    }
}
