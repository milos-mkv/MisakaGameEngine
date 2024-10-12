package org.misaka.gui.components;

import imgui.ImGui;
import org.misaka.core.Scene;
import org.misaka.gui.GameEngineUIComponent;
import org.misaka.managers.SceneManager;

public class ActiveSceneWindow implements GameEngineUIComponent {

    public ActiveSceneWindow() {
    }

    @Override
    public void render() {
        Scene scene = SceneManager.getActiveScene();

        ImGui.begin("Scene");
        if (scene != null) {
            ImGui.image(scene.getFrameBuffer().getTexture(),
                    ImGui.getWindowSizeX(), ImGui.getWindowSizeY(), 0, 1, 1, 0);

        }
        ImGui.end();
    }
}
