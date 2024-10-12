package org.misaka.core.components;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import org.misaka.core.Component;
import org.misaka.factory.TextureFactory;
import org.misaka.gfx.Texture;

import java.nio.file.Path;
import java.nio.file.Paths;

@Data
public class SpriteComponent extends Component {

    private String filePath;
    @JsonIgnore
    private Texture texture;

    public SpriteComponent() {
        super("Sprite");
    }

    public void setTexture(String path) {
        this.filePath = path;
        this.texture = TextureFactory.createTexture(Paths.get(path));
    }
}

