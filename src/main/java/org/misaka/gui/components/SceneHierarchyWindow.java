package org.misaka.gui.components;

import imgui.ImGui;
import imgui.flag.ImGuiHoveredFlags;
import imgui.flag.ImGuiTreeNodeFlags;
import org.joml.Vector3f;
import org.misaka.core.GameObject;
import org.misaka.core.Scene;
import org.misaka.core.components.TransformComponent;
import org.misaka.engine.EngineCameraController;
import org.misaka.engine.EngineEventManager;
import org.misaka.factory.GameObjectFactory;
import org.misaka.factory.SceneFactory;
import org.misaka.gui.GameEngineUI;
import org.misaka.gui.GameEngineUIComponent;
import org.misaka.managers.SceneManager;
import org.misaka.utils.Icons;

import java.awt.*;

public class SceneHierarchyWindow implements GameEngineUIComponent {

    private GameObject gameObjectToMove;
    private GameObject gameObjectToMoveTo;
    private int gameObjectToMoveNewIndex;

    private GameObject gameObjectToDelete;

    public SceneHierarchyWindow() {
        gameObjectToMove = null;
        gameObjectToMoveTo = null;
        gameObjectToDelete = null;
        gameObjectToMoveNewIndex = 0;
    }

    @Override
    public void render() {
        Scene scene = SceneManager.getActiveScene();
        ImGui.begin(Icons.List +" Scene Hierarchy");

        displayWindowContextMenu();

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
        boolean open = ImGui.treeNodeEx(Icons.Cube + " " + gameObject.getName(), flag);
        if (ImGui.isItemHovered(ImGuiHoveredFlags.None)) {
            if (ImGui.isMouseDoubleClicked(0)) {
                EngineCameraController
                        .getInstance().getTransform().getPosition().x = gameObject.getComponent(TransformComponent.class).getPosition().x;
                EngineCameraController
                        .getInstance().getTransform().getPosition().y = gameObject.getComponent(TransformComponent.class).getPosition().y;
            }}
        if (ImGui.isItemClicked()) {
            GameEngineUI.getInstance()
                    .getComponent(GameObjectInspectorWindow.class)
                    .setGameObject(gameObject);
        }

        if (ImGui.beginDragDropSource()) {
            ImGui.setDragDropPayload("GameObject", gameObject);
            ImGui.text(gameObject.getName());
            ImGui.endDragDropSource();
        }

        // NOTE: Moving object directly to another game object its just addition to list.
        setDragTargetForGameObjectReorder(gameObject.getChildren().size(), gameObject);

        if (open) {


            displayGameObjectContextMenu(gameObject);
            int index = 0;
            for (GameObject childGameObject : gameObject.getChildren()) {
                ImGui.separator();
                setDragTargetForGameObjectReorder(index, gameObject);
                displayGameObject(childGameObject);
                index++;
            }
            ImGui.treePop();
        } else {
            displayGameObjectContextMenu(gameObject);
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
            if (gameObjectToMove.getParent().get() == gameObjectToMoveTo) {
                attachGameObjectToSameParent();
            } else {
                attachGameObjectToNewParent();
            }
        }

        if (gameObjectToDelete != null) {
            gameObjectToDelete.removeFromParent();
            EngineEventManager.dispatch("GameObjectDeleted", gameObjectToDelete);
            gameObjectToDelete = null;
        }

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

    private void displayWindowContextMenu() {
        Scene scene = SceneManager.getActiveScene();
        if (ImGui.beginPopupContextWindow()) {
            if (ImGui.menuItem("New Game Object")) {
                scene.addGameObject(GameObjectFactory.createGameObject());
            }
            ImGui.endPopup();
        }
    }

    private void displayGameObjectContextMenu(GameObject gameObject) {
        if (ImGui.beginPopupContextItem()) {
            if (ImGui.menuItem("Delete")) {
                gameObjectToDelete = gameObject;
            }
            ImGui.endPopup();
        }
    }

}
