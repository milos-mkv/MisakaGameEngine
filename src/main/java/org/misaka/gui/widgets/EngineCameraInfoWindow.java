package org.misaka.gui.widgets;

import imgui.ImGui;
import org.misaka.core.Settings;
import org.misaka.engine.EngineCameraController;
import org.misaka.gui.GameEngineUIComponent;

import java.text.NumberFormat;

public class EngineCameraInfoWindow implements GameEngineUIComponent {

    public EngineCameraInfoWindow() {

    }

    @Override
    public void render() {
        EngineCameraController camera = EngineCameraController.getInstance();
        ImGui.begin("Engine Camera Info");
        var pos = camera.getTransform().getPosition();
        ImGui.text(String.valueOf(pos.x) + " " + String.valueOf(pos.y) + " " + pos.z);
        ImGui.text(String.valueOf(camera.getTransform().getRotation()) );
        var scale = camera.getTransform().getScale();
        ImGui.text(scale.x + " " + scale.y + " " + scale.z);

        ImGui.text(camera.getViewport().x + " " + camera.getViewport().y);
        if (ImGui.button("Use Camera")) {
            Settings.useCamera = !Settings.useCamera;
        }
        ImGui.end();
    }
}
