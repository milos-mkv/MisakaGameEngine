package org.misaka.gfx;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL20.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ShaderProgram {

    private int id;

    public void use() {
        glUseProgram(id);
    }

    public void setUniformMatrix4(String name, Matrix4f matrix) {
        var buffer = new float[16];
        matrix.get(buffer);
        glUniformMatrix4fv(glGetUniformLocation(id, name), false, buffer);
    }

}
