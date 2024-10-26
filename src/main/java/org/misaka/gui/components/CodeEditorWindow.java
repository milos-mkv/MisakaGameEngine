package org.misaka.gui.components;

import imgui.ImGui;
import imgui.extension.texteditor.TextEditor;
import imgui.extension.texteditor.TextEditorLanguageDefinition;
import imgui.flag.ImGuiCond;
import imgui.type.ImBoolean;
import org.misaka.gui.GameEngineUIComponent;

import java.nio.file.Path;

public class CodeEditorWindow implements GameEngineUIComponent {

    private ImBoolean open;
    private Path filePath;
    private TextEditor editor;

    public CodeEditorWindow(Path path) {
        this.filePath = path;
        editor = new TextEditor();
        TextEditorLanguageDefinition lang = TextEditorLanguageDefinition.lua();
        editor.setLanguageDefinition(lang);
        open = new ImBoolean(true);
    }

    @Override
    public void render() {
        if (!open.get()) {
            return;
        }
        ImGui.setNextWindowDockID(27, ImGuiCond.FirstUseEver); // Dock to the main dock space
        ImGui.begin(filePath.getFileName().toString(), open);
        editor.render(filePath.toString());
        ImGui.end();
    }
}

