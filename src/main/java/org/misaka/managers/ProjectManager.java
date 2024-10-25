package org.misaka.managers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.Data;
import lombok.Getter;
import org.misaka.app.Project;
import org.misaka.app.ProjectConfiguration;
import org.misaka.utils.Utils;

import java.nio.file.Path;
import java.nio.file.Paths;

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

    public void loadProject(Path path) {

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

}
