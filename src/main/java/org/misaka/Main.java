package org.misaka;

import org.misaka.app.GameEngineEditor;

public class Main {
    public static void main(String[] args) {
        GameEngineEditor.getInstance().run();
        GameEngineEditor.getInstance().dispose();
    }
}