package org.misaka.factory;

import lombok.Data;
import lombok.Getter;
import org.misaka.gfx.ShaderProgram;
import org.misaka.utils.Utils;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

public abstract class ShaderFactory {

    @Getter
    private static final Map<String, ShaderProgram> shaders = new HashMap<>();

    @Data
    private static class Shader {

        protected int id;

        public Shader(int type, String code) {
            id = glCreateShader(type);
            glShaderSource(id, code);
            glCompileShader(id);

            String err = glGetShaderInfoLog(id, glGetShaderi(id, GL_INFO_LOG_LENGTH));
            if (glGetShaderi(id, GL_COMPILE_STATUS) == GL_FALSE) {
                System.out.println("Failed to compile shader program: " + err);
            }
        }

        public void dispose() {
            glDeleteShader(id);
        }
    }

    public static ShaderProgram createShaderProgram(String name, Path vertexShaderFilePath, Path fragmentShaderFilePath) {
        String vertexShaderCode = Utils.readFromFile(vertexShaderFilePath);
        String fragmentShaderCode = Utils.readFromFile(fragmentShaderFilePath);
        return createShaderProgram(name, vertexShaderCode, fragmentShaderCode);
    }

    public static ShaderProgram createShaderProgram(String name, String vertexShaderCode, String fragmentShaderCode) {
        Shader vert = new Shader(GL_VERTEX_SHADER, vertexShaderCode);
        Shader frag = new Shader(GL_FRAGMENT_SHADER, fragmentShaderCode);

        int id = glCreateProgram();
        glAttachShader(id, vert.getId());
        glAttachShader(id, frag.getId());
        glLinkProgram(id);

        String err = glGetProgramInfoLog(id, glGetProgrami(id, GL_INFO_LOG_LENGTH));
        if (glGetProgrami(id, GL_LINK_STATUS) == GL_FALSE) {
            System.out.println(err);
        }

        vert.dispose();
        frag.dispose();

        ShaderProgram shaderProgram = new ShaderProgram(id);
        shaders.put(name, shaderProgram);
        return shaderProgram;
    }

    public static void disposeShaderProgram(ShaderProgram shaderProgram) {
        if (shaderProgram != null) {
            glDeleteProgram(shaderProgram.getId());
        }
    }

}
