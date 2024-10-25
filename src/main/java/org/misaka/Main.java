package org.misaka;

import org.misaka.app.GameEngine;

import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        GameEngine.getInstance().run();
        GameEngine.getInstance().dispose();

    }
}