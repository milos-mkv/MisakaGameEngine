package org.misaka.gui.components;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.extension.imguizmo.ImGuizmo;
import imgui.extension.imguizmo.flag.Mode;
import imgui.extension.imguizmo.flag.Operation;
import imgui.flag.ImGuiMouseButton;
import imgui.flag.ImGuiStyleVar;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.misaka.core.GameObject;
import org.misaka.core.Scene;
import org.misaka.core.Settings;
import org.misaka.core.components.TransformComponent;
import org.misaka.engine.EngineCameraController;
import org.misaka.gui.GameEngineUI;
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
                    .zoom((float) (ImGui.getIO().getMouseWheel() * 0.1));
        }

        mouseLastPosition = currentMouseCursor;

        manipulate();

        ImGui.end();
        ImGui.popStyleVar();
    }

    private void manipulate() {
        GameObject gameObject = GameEngineUI.getInstance().getComponent(GameObjectInspectorWindow.class).getGameObject();

        if (gameObject == null) {
            return;
        }

        ImGuizmo.setOrthographic(false);
        ImGuizmo.setEnabled(true);
        ImGuizmo.setDrawList();
        ImGuizmo.setRect(ImGui.getWindowPosX(), ImGui.getWindowPosY(), ImGui.getWindowWidth(), ImGui.getWindowHeight());
        TransformComponent transform = gameObject.getComponent(TransformComponent.class);



        var view = matrix4x4ToFloatBuffer(EngineCameraController.getInstance().getViewMatrix().invert());
        var proj = matrix4x4ToFloatBuffer(EngineCameraController.getInstance().getProjectionMatrix());
        var tran = matrix4x4ToFloatBuffer(gameObject.getWorldTransform());

        ImGuizmo.manipulate(view, proj, tran, Operation.TRANSLATE, Mode.WORLD);

        if (ImGuizmo.isUsing()) {
            Matrix4f calculatedMatrix = new Matrix4f().set(tran);
//            calculatedMatrix.getTranslation(transform.getPosition());
            gameObject.setWorldTransform(calculatedMatrix);
        }

    }

    static float[] matrix4x4ToFloatBuffer(Matrix4f matrix4f) {
        var buffer = new float[16];
        matrix4f.get(buffer);
        return buffer;
    }
}
