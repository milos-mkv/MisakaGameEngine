#version 330 core

layout (location = 0) in vec3 pos;

uniform mat4 model, view, projection;

out vec3 worldPos; // Pass world position to fragment shader

void main() {
    // Compute world position (without view and projection transformations)
    worldPos = vec3(model * vec4(pos, 1.0));

    // Transform the vertex position for rendering
    gl_Position = projection * view * model * vec4(pos, 1.0);
}