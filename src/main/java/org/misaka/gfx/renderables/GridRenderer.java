package org.misaka.gfx.renderables;

import lombok.Getter;
import org.joml.Vector3f;
import org.misaka.core.components.TransformComponent;
import org.misaka.engine.EngineCameraController;
import org.misaka.factory.ShaderFactory;

import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public class GridRenderer {

    private final int vbo;
    private final int vao;
    private final int ebo;
    private final TransformComponent transform;

    public GridRenderer() {
        var vertices = new float[] {
                0.5f,  0.5f, 0.0f, 1.0f, 1.0f,
                0.5f, -0.5f, 0.0f, 1.0f, 0.0f,
                -0.5f, -0.5f, 0.0f, 0.0f, 0.0f,
                -0.5f,  0.5f, 0.0f, 0.0f, 1.0f
        };
        var indices = new int[] { 0,1,3,1,2,3 };
        vao = glGenVertexArrays();
        vbo = glGenBuffers();
        ebo = glGenBuffers();
        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 5 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 5 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        ShaderFactory.createShaderProgram("Grid",
                Paths.get("./src/main/resources/shaders/grid.vert"),
                Paths.get("./src/main/resources/shaders/grid.frag")
        );

        transform = new TransformComponent();
        transform.setScale(new Vector3f(3000, 3000, 1));
    }

    public void render() {
        var shader = ShaderFactory.getShaders().get("Grid");
        var camera = EngineCameraController.getInstance();

        transform.setPosition(camera.getTransform().getPosition());
        shader.use();
        shader.setUniformMatrix4("model", transform.getTransformMatrix());
        shader.setUniformMatrix4("projection", camera.getProjectionMatrix());
        shader.setUniformMatrix4("view", camera.getViewMatrix().invert());

        glBindVertexArray(vao);
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
    }

    public void render1() {
        var shader = ShaderFactory.getShaders().get("Grid1");
        var camera = EngineCameraController.getInstance();

        transform.setPosition(camera.getTransform().getPosition());
        shader.use();
        shader.setUniformMatrix4("model", transform.getTransformMatrix());
        shader.setUniformMatrix4("projection", camera.getProjectionMatrix());
        shader.setUniformMatrix4("view", camera.getViewMatrix().invert());

        glBindVertexArray(vao);
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
    }

    public void dispose() {
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
        glDeleteBuffers(ebo);
    }

}
