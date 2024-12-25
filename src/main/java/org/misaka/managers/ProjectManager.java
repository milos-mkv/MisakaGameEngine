package org.misaka.managers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.Data;
import lombok.Getter;
import org.misaka.app.Project;
import org.misaka.app.ProjectConfiguration;
import org.misaka.core.Scene;
import org.misaka.factory.GameObjectFactory;
import org.misaka.factory.SceneFactory;
import org.misaka.gfx.FrameBuffer;
import org.misaka.utils.Utils;

import java.io.IOException;
import java.nio.file.*;
import java.util.LinkedHashMap;

@Data
public class ProjectManager {

    @Getter
    private static final ProjectManager instance = new ProjectManager();

    private Project project;
    private ObjectMapper objectMapper;

    public ProjectManager() {
        project = null;
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    public boolean loadProject(Path path) {
        Path configPath = Paths.get(path.toString(), "misaka-config.json");
        String json = Utils.readFromFile(configPath);
        Project project = null;
        try {
            project = objectMapper.readValue(json, Project.class);
            System.out.println(project);
        } catch (JsonProcessingException e) {
            System.out.println(e);
            return false;
        }

        // Validate project directory structure.
        String[] essentialDirectories = new String[] { "scenes", "scripts", "assets" };
        for (String directory : essentialDirectories) {
            Path pathToDirectory = Paths.get(path.toString(), directory);
            if (!Utils.doesDirectoryExists(pathToDirectory.toString())) {
                System.out.println("Directory : " + pathToDirectory + " does not exist!");
                return false;
            }
        }

        project.setPath(path.toString());

        // TODO: Load scenes.
        Path scenesDirectory = Paths.get(path.toString(), "scenes");
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(scenesDirectory)) {
            for (Path entry : stream) {
                if (Files.isDirectory(entry)) {
                    continue;
                }
                String ext = Utils.getFileExtension(entry.getFileName().toString());
                if (!ext.equals("scene")) {
                    continue;
                }
                String sceneJsonData = Utils.readFromFile(entry);
                Scene scene = SceneFactory.generateSceneFromJson(sceneJsonData);
                project.setScenes(new LinkedHashMap<>());
                project.getScenes().put(scene.getName(), scene);
            }
        } catch (IOException | DirectoryIteratorException e) {
            System.out.println(e);
        }

        this.project = project;
        return true;
    }

    /**
     * Create new project.
     * @param name Project name.
     * @param path Path to project directory.
     * @return Project
     */
    public Project createProject(String name, String path) {
        Path fullpath = Paths.get(path, name);

        if (!Utils.createDirectory(fullpath.toString())) {
            System.out.println("Failed to create directory: " + fullpath);
            return null;
        }

        Project project = new Project(name, fullpath.toString());

        String[] essentialDirectories = new String[] { "scenes", "scripts", "assets" };
        for (String directory : essentialDirectories) {
            Path pathToDirectory = Paths.get(fullpath.toString(), directory);
            if (!Utils.createDirectory(pathToDirectory.toString())) {
                System.out.println("Failed to create directory: " + pathToDirectory);
                return null;
            }
        }

        try {
            String json = objectMapper.writeValueAsString(project);
            Path projectConfig = Paths.get(fullpath.toString(), "misaka-config.json");
            Utils.saveToFile(projectConfig, json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        
        return project;
    }

    public void saveProject() {
        // TODO: Save current project.
    }

}
