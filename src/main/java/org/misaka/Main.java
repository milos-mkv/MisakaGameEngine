package org.misaka;

import org.misaka.app.GameEngine;

public class Main {
    public static void main(String[] args) {
        GameEngine.getInstance().run();
        GameEngine.getInstance().dispose();
    }
}