package org.misaka.gfx;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.nio.file.Path;

@Data
@AllArgsConstructor
public class Texture {

    private Path path;
    private int id;
    private int width;
    private int height;

}
