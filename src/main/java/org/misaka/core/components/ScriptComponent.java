package org.misaka.core.components;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.luaj.vm2.LuaValue;
import org.misaka.core.Component;
import org.misaka.core.Scene;

import java.nio.file.Path;

@Data
public class ScriptComponent extends Component {

    private String filePath;
    @JsonIgnore
    private LuaValue behaviourTable;

    public ScriptComponent() {
        super("Script");
    }

    public void addScriptFile(Scene scene, Path filePath) {
        this.filePath = filePath.toString();
        if (filePath != null) {
            LuaValue module = scene.getGlobals().loadfile(filePath.toString());
            this.behaviourTable = module.call();
        }
    }

}
