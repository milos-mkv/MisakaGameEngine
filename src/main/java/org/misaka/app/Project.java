package org.misaka.app;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.misaka.core.Scene;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonDeserialize
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Project {

    private String name;

    @JsonIgnore
    private String path;

    @JsonIgnore
    Map<String, Scene> scenes;


    public Project(String name, String path) {
        this.name = name;
        this.path = path;
        this.scenes = new LinkedHashMap<>();
    }
}
