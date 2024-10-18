package org.misaka.gfx;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joml.Matrix4f;
import org.joml.Vector3f;

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

    public void setUniformBoolean(String name, int value) {
        glUniform1i(glGetUniformLocation(id, name), value);
    }

    public void setUniformVec3(String name, Vector3f value) {
        glUniform3f(glGetUniformLocation(id, name), value.x, value.y, value.z);
    }

    public void setUniformFloat(String name, float value) {
        glUniform1f(glGetUniformLocation(id, name), value);
    }

    public void setUniformInt(String name, int value) {
        glUniform1i(glGetUniformLocation(id, name), value);
    }
}
