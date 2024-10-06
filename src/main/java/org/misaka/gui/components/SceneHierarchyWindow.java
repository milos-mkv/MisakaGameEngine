package org.misaka.gui.components;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import org.misaka.core.GameObject;
import org.misaka.core.Scene;
import org.misaka.gui.GameEngineUIComponent;
import org.misaka.managers.SceneManager;

public class SceneHierarchyWindow implements GameEngineUIComponent {

    private GameObject gameObjectToMove;
    private GameObject gameObjectToMoveTo;
    private int gameObjectToMoveNewIndex;

    public SceneHierarchyWindow() {
        gameObjectToMove = null;
        gameObjectToMoveTo = null;
        gameObjectToMoveNewIndex = 0;
    }

    @Override
    public void render() {
        Scene scene = SceneManager.getActiveScene();
        ImGui.begin("Scene Hierarchy Window");
        if (scene != null) {
            ImGui.text(scene.getName());

            int index = 0;
            for (GameObject gameObject : scene.getRootGameObject().getChildren()) {
                ImGui.separator();
                setDragTargetForGameObjectReorder(index, scene.getRootGameObject());

                displayGameObject(gameObject);
                index++;
            }

        }
        ImGui.end();

        process();
    }

    private void displayGameObject(GameObject gameObject) {
        ImGui.pushID(gameObject.getId().toString());
        int flag = (ImGuiTreeNodeFlags.SpanFullWidth | ImGuiTreeNodeFlags.OpenOnArrow);
        boolean open = ImGui.treeNodeEx(gameObject.getName(), flag);
        if (ImGui.beginDragDropSource()) {
            ImGui.setDragDropPayload("GameObject", gameObject);
            ImGui.text(gameObject.getName());
            ImGui.endDragDropSource();
        }

        // NOTE: Moving object directly to another game object its just addition to list.
        if (ImGui.beginDragDropTarget()) {
            GameObject payload = ImGui.acceptDragDropPayload("GameObject");
            if (payload != null && payload != gameObject && gameObject != payload.getParent()) {
                gameObjectToMove = payload;
                gameObjectToMoveTo = gameObject;
                gameObjectToMoveNewIndex = gameObject.getChildren().size();
            }
            ImGui.endDragDropTarget();
        }

        if (open) {
            int index = 0;
            for (GameObject childGameObject : gameObject.getChildren()) {
                ImGui.separator();
                setDragTargetForGameObjectReorder(index, gameObject);
                displayGameObject(childGameObject);
                index++;
            }
            ImGui.treePop();
        }
        ImGui.popID();
    }

    private void setDragTargetForGameObjectReorder(int index, GameObject gameObject) {
        if (ImGui.beginDragDropTarget()) {
            GameObject payload = ImGui.acceptDragDropPayload("GameObject");
            if (payload != null && payload != gameObject) {
                gameObjectToMoveTo = gameObject;
                gameObjectToMove = payload;
                gameObjectToMoveNewIndex = index;
            }
            ImGui.endDragDropTarget();
        }
    }

    private void process() {

        if (gameObjectToMove != null) {
            if (gameObjectToMove.getParent() == gameObjectToMoveTo) {
                attachGameObjectToSameParent();
            } else {
                attachGameObjectToNewParent();
            }
        }

        // Reset these.
        gameObjectToMoveTo = null;
        gameObjectToMove = null;
        gameObjectToMoveNewIndex = 0;
    }

    private void attachGameObjectToNewParent() {
        gameObjectToMoveTo.addChild(gameObjectToMove, gameObjectToMoveNewIndex);
    }

    private void attachGameObjectToSameParent() {
        int index = gameObjectToMoveTo.getChildIndex(gameObjectToMove);
        if (gameObjectToMoveNewIndex == index || gameObjectToMoveNewIndex == index + 1) {
            return;
        }
        gameObjectToMoveTo.getChildren().set(index, null);
        gameObjectToMoveTo.addChild(gameObjectToMove, gameObjectToMoveNewIndex);
        System.out.println(gameObjectToMoveNewIndex);
        gameObjectToMoveTo.removeChild(null);
    }
}
