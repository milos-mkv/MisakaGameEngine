package org.misaka.core.components;

import lombok.Data;
import org.luaj.vm2.LuaValue;
import org.misaka.core.Component;
import org.misaka.core.Scene;

import java.nio.file.Path;

@Data
public class ScriptComponent extends Component {

    private Path filePath;
    private LuaValue behaviourTable;

    public ScriptComponent() {
        super("Script");
    }

    public void addScriptFile(Scene scene, Path filePath) {
        this.filePath = filePath;
        LuaValue module = scene.getGlobals().loadfile(filePath.toString());
        this.behaviourTable = module.call();
    }

}
