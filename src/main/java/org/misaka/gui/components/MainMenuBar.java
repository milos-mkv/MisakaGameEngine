package org.misaka.gui.components;

import imgui.ImGui;
import org.lwjgl.glfw.GLFW;
import org.misaka.app.GameEngineEditor;
import org.misaka.gui.GameEngineUIComponent;
import org.misaka.gui.popups.CreateNewProjectPopup;
import org.misaka.managers.ProjectManager;
import org.misaka.utils.Utils;

import java.nio.file.Paths;

public class MainMenuBar implements GameEngineUIComponent {

    public MainMenuBar() {
    }

    @Override
    public void render() {
        boolean newProjectAction = false;
        boolean openProjectAction = false;
        boolean saveProjectAction = false;
        boolean exitAppAction = false;

        if (ImGui.beginMainMenuBar()) {
            if (ImGui.beginMenu("File")) {
                newProjectAction = ImGui.menuItem("New Project", "Ctrl+N");
                openProjectAction = ImGui.menuItem("Open Project", "Ctrl+O");
                saveProjectAction = ImGui.menuItem("Save", "Ctrl+S");
                ImGui.separator();
                exitAppAction = ImGui.menuItem("Exit", "Ctrl+W");
                ImGui.endMenu();
            }
            ImGui.setCursorPosX(ImGui.getWindowWidth() - 100);
            ImGui.textDisabled("FPS: " + String.valueOf((int)ImGui.getIO().getFramerate()));
            ImGui.endMainMenuBar();
        }

        if (newProjectAction) {
            ImGui.openPopup("Create New Project Popup");
        }

        if (openProjectAction) {
            openProject();
        }

        if (saveProjectAction) {
            saveProject();
        }

        if (exitAppAction) {
            exitApp();
        }

        CreateNewProjectPopup.render();
    }


    private void openProject() {
        String path = Utils.openFolderDialog();
        if (path != null) {
            ProjectManager.getInstance().loadProject(Paths.get(path));
        }
    }

    private void saveProject() {
        ProjectManager.getInstance().saveProject();
    }

    private void exitApp() {
        // TODO: Change this.
        GLFW.glfwSetWindowShouldClose(GameEngineEditor.getInstance().getEditorWindow().getHandle(), true);
    }
}
