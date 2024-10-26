package org.misaka.gui.widgets;


import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiHoveredFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;
import org.misaka.engine.EngineAssetManager;
import org.misaka.factory.TextureFactory;
import org.misaka.gfx.Texture;
import org.misaka.gui.GameEngineUI;
import org.misaka.gui.GameEngineUIComponent;
import org.misaka.gui.components.CodeEditorWindow;
import org.misaka.utils.Utils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FolderExplorer implements GameEngineUIComponent {

    private Path rootPath;
    private Path selectedDirectoryPath;
    private final List<Path> selectedDirectoryData;
    private final List<String> selectedDirectoryDepthData;
    private ImString searchBarTxt;

    /**
     * Constructor.
     * @param rootPath Root path of folder explorer.
     */
    public FolderExplorer(Path rootPath) {
        this.selectedDirectoryData = new ArrayList<>();
        this.selectedDirectoryDepthData = new ArrayList<>();
        this.searchBarTxt = new ImString();
        setRootPath(rootPath.toAbsolutePath());
    }

    /**
     * Change root path of folder explorer.
     * @param rootPath New root path.
     */
    public void setRootPath(Path rootPath) {
        this.rootPath = rootPath.toAbsolutePath();
        setSelectedDirectoryPath(this.rootPath);
    }

    /**
     * Select directory which contents will be displayed in explorer.
     * @param selectedDirectoryPath Directory path.
     */
    public void setSelectedDirectoryPath(Path selectedDirectoryPath) {
        this.selectedDirectoryPath = selectedDirectoryPath;
        loadDirectoryData();
    }

    /**
     * Load all files and directories from current selected directory.
     */
    private void loadDirectoryData() {

        this.selectedDirectoryData.clear();
        List<Path> files = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(this.selectedDirectoryPath)) {
            for (Path entry : stream) {
                if (Files.isDirectory(entry)) {
                    selectedDirectoryData.add(entry);
                }
                else {
                    files.add(entry);
                }
            }
        } catch (IOException | DirectoryIteratorException e) {
        }
        selectedDirectoryData.addAll(files);

        // Load all directory names from root to current selected directory.
        Path relativePath = rootPath.relativize(selectedDirectoryPath);
        selectedDirectoryDepthData.clear();
        selectedDirectoryDepthData.add(rootPath.getFileName().toString());

        if (!selectedDirectoryPath.toAbsolutePath().toString().equals(rootPath.toAbsolutePath().toString()))  {
            for (Path part : relativePath) {
                selectedDirectoryDepthData.add(part.toString());
            }
        }

    }

    @Override
    public void render() {
        ImGui.beginChild(rootPath.toString(), -1, -1, false, ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse);
        ImGui.pushStyleColor(ImGuiCol.ChildBg, 0.05f, 0.05f, 0.05f, 1.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.FrameBorderSize, 0);
        ImGui.beginChild("Path trail", -200, 39);
        ImGui.setCursorPos(10, 10);
        ImGui.text("\uf07c");
        ImGui.sameLine();
        ImGui.setCursorPosY(5);

        for (int i = 0; i < selectedDirectoryDepthData.size(); i++) {
            if (ImGui.button(selectedDirectoryDepthData.get(i))) {
                if (i == 0) {
                    setSelectedDirectoryPath(rootPath);
                } else {
                    StringBuilder dir = new StringBuilder();
                    for (int j = 1; j < i + 1; j++) {
                        dir.append("/").append(selectedDirectoryDepthData.get(j));
                    }
                    setSelectedDirectoryPath(Paths.get(rootPath.toAbsolutePath() + dir.toString()));
                }
            }
            ImGui.sameLine();
            if (i+1 < selectedDirectoryDepthData.size()) {
                ImGui.setCursorPosY(7);
                ImGui.text("\uf0da");
                ImGui.sameLine();
                ImGui.setCursorPosY(5);
            }
        }

        ImGui.endChild();
        ImGui.sameLine();

        ImGui.beginChild("Search bar", -1, 39);
        ImGui.setCursorPos(10, 10);
        ImGui.text("\uf0b0");
        ImGui.sameLine();
        ImGui.setCursorPosY(5);

        ImGui.inputText("##Search", searchBarTxt);
        ImGui.endChild();

        ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0, 0, 0, 0.4F);
        ImGui.pushFont(EngineAssetManager.getInstance().getFonts().get("Default15"));

        ImGui.beginChild("Directory data window", -1, -20);
        if (ImGui.beginPopupContextWindow()) {
            if (ImGui.menuItem("New File")) {
            }
            if (ImGui.menuItem("New Folder")) {
            }
            ImGui.endPopup();
        }
        Path clickedDirectory = null;
        Path clickDeleteFilePath = null;
        Path clickRenameFilePath = null;
        for (Path entry : selectedDirectoryData) {
            String filename = Utils.getFilenameWithoutExtension(entry.getFileName().toString());

            if (!searchBarTxt.get().isEmpty() && !filename.contains(searchBarTxt.get())) {
                continue;
            }

            ImVec2 cur = ImGui.getCursorPos();
            ImGui.setCursorPos(cur.x + 5, cur.y);

            String ext = Utils.getFileExtension(entry.getFileName().toString());
            String image;
            if(Files.isDirectory(entry)) {
                image = "FolderIcon";
            } else {

                if (Objects.equals(ext, "png") ||Objects.equals(ext, "jpg") || Objects.equals(ext, "jpeg") ) {
                    image = "ImageFileIcon";
                } else
                if (Objects.equals(ext, "lua")) {
                    image = "LuaFileIcon";
                }else if (Objects.equals(ext, "json")) {
                    image = "JsonFileIcon";
                }else if (Objects.equals(ext, "scene")) {
                    image = "SceneFileIcon";
                }
                else {
                    image = "FileIcon";
                }
            }
            if (Utils.isImageFile(ext)) {
                Texture t = TextureFactory.createTexture(entry.toAbsolutePath());

                double widthRatio = (double) 90 / t.getWidth();
                double heightRatio = (double) 90 / t.getHeight();
                double scaleFactor = Math.min(widthRatio, heightRatio);

                int newWidth = (int) (t.getWidth() * scaleFactor);
                int newHeight = (int) (t.getHeight() * scaleFactor);
                ImGui.setCursorPos(cur.x + 5 + ((90 - newWidth) / 2) , cur.y + ((90 - newHeight) / 2));

                ImGui.image(t.getId(), newWidth, newHeight, 0, 1, 1, 0);
            }
            else
                ImGui.image(EngineAssetManager.getInstance().getImages().get(image).getId(), 90, 90, 0, 1, 1, 0);
            ImGui.setCursorPos(cur.x, cur.y + 85);
            renderTruncatedText(filename, 90);
            ImGui.setCursorPos(cur.x, cur.y);

            if (ImGui.button("##"+filename, 100, 110)) {
                if (Files.isDirectory(entry)) {
                    clickedDirectory = entry;
                }
            }
            if (ImGui.isItemHovered(ImGuiHoveredFlags.None)) {
                if (ImGui.isMouseDoubleClicked(0) && Desktop.isDesktopSupported()) {
                    if (!Files.isDirectory(entry) && Objects.equals(ext, "lua")) {
                        System.out.println("ASD");
                        GameEngineUI.getInstance().addCodeEditor(entry);

//                        Desktop desktop = Desktop.getDesktop();
//                        try {
//                            desktop.open(new File(entry.toString()));
//                        } catch (IOException e) {
//                        }
                    }
                }
                ImGui.beginTooltip();
                ImGui.textDisabled(entry.toString());
                ImGui.endTooltip();
            }
            if (ImGui.beginPopupContextItem()) {
                if (ImGui.menuItem("Delete")) {
                    clickDeleteFilePath = entry;
                }
                if (ImGui.menuItem("Rename")) {
                    clickRenameFilePath = entry;
                }
                ImGui.endPopup();
            }
            if((cur.x + 200) < ImGui.getWindowWidth())  {
                ImGui.sameLine();
            }
        }


        ImGui.endChild();

        ImGui.textDisabled(selectedDirectoryPath.toAbsolutePath().toString());
        ImGui.popFont();

        ImGui.popStyleColor(3);
        ImGui.popStyleVar();
        ImGui.endChild();

        if (clickedDirectory != null) {
            setSelectedDirectoryPath(clickedDirectory);
        }

        if (clickDeleteFilePath != null) {
            // TODO: Delete
        }

        if (clickRenameFilePath != null) {
            // TODO: Rename
        }
    }

    /**
     * Render text with max width.
     * @param text Text.
     * @param maxWidth Max width of text.
     */
    private void renderTruncatedText(String text, float maxWidth) {
        float textWidth = ImGui.calcTextSize(text).x;
        if (textWidth > maxWidth) {
            int charCount = text.length();
            while (charCount > 0 && ImGui.calcTextSize(text.substring(0, charCount) + "...").x > maxWidth) {
                charCount--;
            }
            text = text.substring(0, charCount) + "...";
        }
        ImVec2 size = ImGui.calcTextSize(text);
        ImGui.setCursorPosX(ImGui.getCursorPosX() + (100 - size.x) / 2);
        ImGui.textUnformatted(text);
    }
}
