package org.misaka.app;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import org.misaka.core.Scene;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonDeserialize
@Data
public class Project {

    private String name;
    private String path;

    @JsonIgnore
    Map<String, Scene> scenes;

    public Project(String name, String path) {
        this.name = name;
        this.path = path;
        this.scenes = new LinkedHashMap<>();
    }

}
