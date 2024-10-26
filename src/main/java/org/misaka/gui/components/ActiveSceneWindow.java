package org.misaka.gui.components;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.extension.imguizmo.ImGuizmo;
import imgui.extension.imguizmo.flag.Mode;
import imgui.extension.imguizmo.flag.Operation;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiMouseButton;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import org.joml.Matrix4f;
import org.misaka.app.GameEngine;
import org.misaka.core.GameObject;
import org.misaka.core.Scene;
import org.misaka.core.Settings;
import org.misaka.core.components.TransformComponent;
import org.misaka.engine.EngineCameraController;
import org.misaka.game.Game;
import org.misaka.game.GameConfiguration;
import org.misaka.gui.GameEngineUI;
import org.misaka.gui.GameEngineUIComponent;
import org.misaka.managers.SceneManager;
import org.misaka.utils.Icons;
import org.misaka.utils.Utils;

public class ActiveSceneWindow implements GameEngineUIComponent {

    private int manipulationOperation;
    private ImVec2 mouseLastPosition;
    long cursor;

    public ActiveSceneWindow() {
        mouseLastPosition = new ImVec2(0, 0);

        manipulationOperation = Operation.TRANSLATE;
    }

    @Override
    public void render() {

        Scene scene = SceneManager.getActiveScene();
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);
        int flags = ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse;
        ImGui.begin("Scene", flags);
        ImGui.text(String.valueOf(ImGui.getIO().getFramerate()));
        var startCursorPosition = ImGui.getCursorPos();
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
                    .move((mouseLastPosition.x -currentMouseCursor.x), (currentMouseCursor.y -  mouseLastPosition.y));
        }

        if (ImGui.isWindowHovered() && ImGui.getIO().getMouseWheel() != 0.0f) {
            EngineCameraController.getInstance().zoom((float) (ImGui.getIO().getMouseWheel() * 0.1));
        }

        mouseLastPosition = currentMouseCursor;

        manipulate();
        ImGui.setCursorPos(startCursorPosition.x + 10, startCursorPosition.y + 10);
        renderManipulationToolbar();
        ImGui.sameLine();
        renderEngineCameraInfo();
        ImGui.sameLine();
        renderRunToolbar();

        ImGui.end();
        ImGui.popStyleVar();

    }

    private void renderEngineCameraInfo() {
        var camera = EngineCameraController.getInstance();
        ImGui.text("X:" + camera.getTransform().getPosition().x + " Y:" + camera.getTransform().getPosition().y);
    }

    private void renderManipulationToolbar() {
        ImGui.beginChildFrame(2, 44, 110);
        ImGui.setCursorPosY(ImGui.getCursorPosY() + 4);

        int mode = manipulationOperation;

        if (mode == Operation.TRANSLATE) {
            ImGui.pushStyleColor(ImGuiCol.Text, 0.5f, 1.0f, 0.5f, 1.0f);
        }
        if (ImGui.button(Icons.Translate)) {
            manipulationOperation = Operation.TRANSLATE;
        }
        if (mode == Operation.TRANSLATE) {
            ImGui.popStyleColor();
        }
        if (mode == Operation.ROTATE_Z) {
            ImGui.pushStyleColor(ImGuiCol.Text, 0.5f, 1.0f, 0.5f, 1.0f);
        }
        if (ImGui.button(Icons.Rotate)) {
            manipulationOperation = Operation.ROTATE_Z;
        }
        if (mode == Operation.ROTATE_Z) {
            ImGui.popStyleColor();
        }

        if (mode == Operation.SCALE) {
            ImGui.pushStyleColor(ImGuiCol.Text, 0.5f, 1.0f, 0.5f, 1.0f);
        }
        if (ImGui.button(Icons.Scale)) {
            manipulationOperation = Operation.SCALE;
        }
        if (mode == Operation.SCALE) {
            ImGui.popStyleColor();
        }
        ImGui.endChildFrame();
    }

    private void renderRunToolbar() {
        ImGui.setCursorPosX(ImGui.getWindowWidth() - 120);
        ImGui.beginChildFrame(1, 110, 49);
        ImGui.setCursorPos(5, 5);
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 15, 7);
        ImGui.pushStyleColor(ImGuiCol.Text, 0.5f, 1.0f, 0.5f, 1.0f);

        if (ImGui.button(Icons.Play)) {
        }
        ImGui.popStyleColor();

        ImGui.sameLine();

        if (ImGui.button(Icons.Pause)) {

        }
        ImGui.popStyleVar();
        ImGui.endChildFrame();
    }

    private void manipulate() {
        var gameEngineUI = GameEngineUI.getInstance();

        GameObject gameObject = gameEngineUI.getComponent(GameObjectInspectorWindow.class).getGameObject();
        if (gameObject == null) {
            return;
        }

        ImGuizmo.setOrthographic(false);
        ImGuizmo.setEnabled(true);
        ImGuizmo.setDrawList();
        ImGuizmo.setRect(ImGui.getWindowPosX(), ImGui.getWindowPosY(), ImGui.getWindowWidth(), ImGui.getWindowHeight());

        var engineCamera = EngineCameraController.getInstance();
        var view = Utils.matrixToFloatBuffer(engineCamera.getViewMatrix().invert());
        var proj = Utils.matrixToFloatBuffer(engineCamera.getProjectionMatrix());

        var t = gameObject.getComponent(TransformComponent.class);

        var transform = Utils.matrixToFloatBuffer(t.getWorldTransformMatrix());

        float rot = gameObject.getComponent(TransformComponent.class).getRotation();
        ImGuizmo.manipulate(view, proj, transform, manipulationOperation, Mode.WORLD);

        if (ImGuizmo.isUsing()) {
            t.setTransformFromWorldMatrix(new Matrix4f().set(transform));

            // FIXME: For some reason when using scale it resets rotation. Find why is that.
            if (manipulationOperation == Operation.SCALE) {
                gameObject.getComponent(TransformComponent.class).setRotation(rot);
            }
        }
    }
}
