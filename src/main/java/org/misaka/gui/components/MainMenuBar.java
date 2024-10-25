package org.misaka.gui.components;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImString;
import org.misaka.app.ProjectConfiguration;
import org.misaka.gui.GameEngineUIComponent;
import org.misaka.managers.ProjectManager;
import org.misaka.utils.Utils;

import java.nio.file.Paths;

public class MainMenuBar implements GameEngineUIComponent {

    ImString name = new ImString();
    ImString path = new ImString();
    public MainMenuBar() {

    }

    @Override
    public void render() {
        boolean newProjectAction = false;
        if (ImGui.beginMainMenuBar()) {
            if (ImGui.beginMenu("File")) {
                newProjectAction = ImGui.menuItem("New Project");
                if (ImGui.menuItem("Open Project")) {
                    String path = Utils.openFolderDialog();
                    if (path != null) {
                        ProjectManager.getInstance().loadProject(Paths.get(path));
                    }
                }
                ImGui.endMenu();
            }
            ImGui.endMainMenuBar();
        }

        if (newProjectAction) {
            ImGui.openPopup("Create New Project Popup");
        }
        renderCreateNewProjectPopup();
    }

    private void renderCreateNewProjectPopup() {
        ImVec2 workSize = ImGui.getMainViewport().getWorkSize();
        ImVec2 modalSize = new ImVec2(500, 500);
        ImBoolean unusedOpen = new ImBoolean(true);
        ImGui.setNextWindowPos(workSize.x / 2 - modalSize.x / 2, workSize.y / 2 - modalSize.y / 2);
        ImGui.setNextWindowSize(modalSize.x, modalSize.y);


        var flags = ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse | ImGuiWindowFlags.NoTitleBar;
        if (ImGui.beginPopupModal("Create New Project Popup", unusedOpen, flags)) {
//            runnable.run();
            ImGui.text("Create new project");
            ImGui.separator();

            ImGui.inputText("Name", name);
            ImGui.beginDisabled();
            ImGui.inputText("Path", path);
            ImGui.endDisabled();
            if (ImGui.button("P")) {
                String path = Utils.openFolderDialog();
                if (path != null) {
                    this.path.set(path);
                }
            }
            if (ImGui.button("Close")) {
                ImGui.closeCurrentPopup();
            }
            ImGui.sameLine();
            if (ImGui.button("Create")) {
                ProjectManager.getInstance().createProject(name.get(), path.get());
            }
            ImGui.endPopup();
        }

    }
}
