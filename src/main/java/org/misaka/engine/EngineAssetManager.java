package org.misaka.engine;

import imgui.ImFont;
import imgui.ImFontConfig;
import imgui.internal.ImGui;
import imgui.ImGuiIO;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class EngineAssetManager {

    @Getter
    private static final EngineAssetManager instance = new EngineAssetManager();

    @Getter
    private Map<String, ImFont> fonts;

    public EngineAssetManager() {
        loadFonts();
    }

    private void loadFonts() {
        fonts = new HashMap<>();

        ImGuiIO io = ImGui.getIO();

        ImFontConfig config = new ImFontConfig();
        config.setOversampleH(4);
        config.setOversampleV(4);
        config.setPixelSnapH(false);
        config.setMergeMode(false);

        fonts.put("Default", io.getFonts().addFontFromFileTTF("./src/main/resources/fonts/JetBrainsMono-Medium.ttf", 25.0f, config));
        io.setFontDefault(fonts.get("Default"));
        config.setMergeMode(true);
        short[] iconRanges = { (short) 0xF000, (short) 0xF8FF, (short) 0xE000, (short) 0xE8FF, 0};  // Update according to the FontAwesome version
        io.getFonts().addFontFromFileTTF("src/main/resources/fonts/fa-solid-900.ttf", 25.0f, config, iconRanges);
        io.getFonts().build();
    }

}
