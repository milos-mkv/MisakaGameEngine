package org.misaka.gui.components;

import imgui.ImGui;
import imgui.type.ImBoolean;
import org.misaka.gui.GameEngineUIComponent;

public class ConsoleWindow implements GameEngineUIComponent {

    public ConsoleWindow() {
    }
    @Override
    public void render() {
        ImGui.begin("Console");
        ExampleImGuiColorTextEdit.show(new ImBoolean(true));
        ImGui.end();
    }
}
