package org.misaka.gui.components;

import imgui.ImGui;
import imgui.ImVec2;
import org.misaka.app.Project;
import org.misaka.gui.GameEngineUIComponent;
import org.misaka.gui.widgets.FolderExplorer;
import org.misaka.managers.ProjectManager;

import java.nio.file.Paths;

public class ProjectWindow implements GameEngineUIComponent {

    private FolderExplorer folderExplorer = null;

    public ProjectWindow() {
    }

    @Override
    public void render() {
        Project project = ProjectManager.getInstance().getProject();

        ImGui.begin("Project");
        if (project == null) {
            ImVec2 size = ImGui.calcTextSize("No opened project");
            ImGui.setCursorPos(
                    ImGui.getWindowWidth() / 2 - size.x / 2,
                    ImGui.getWindowHeight() / 2 - size.y / 2
            );
            ImGui.textDisabled("No opened project");
        } else {
            if (folderExplorer == null) {
                folderExplorer = new FolderExplorer(Paths.get(project.getPath()));
            }
            folderExplorer.render();

        }
        ImGui.end();
    }
}
