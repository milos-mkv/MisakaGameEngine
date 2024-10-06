package org.misaka;

import org.lwjgl.glfw.GLFW;
import org.misaka.app.GameEngine;
import org.misaka.core.GameObject;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        GameEngine.getInstance().run();
        GameEngine.getInstance().dispose();

    }
}