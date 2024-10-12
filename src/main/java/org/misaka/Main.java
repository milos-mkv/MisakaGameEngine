package org.misaka;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.misaka.app.GameEngine;
import org.misaka.core.components.TransformComponent;

public class Main {
    public static void main(String[] args) {
        GameEngine.getInstance().run();
        GameEngine.getInstance().dispose();

    }
}