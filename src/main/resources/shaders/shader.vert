#version 330 core

layout (location = 0) in vec3 pos;
layout (location = 1) in vec2 aTexCoord;   // Texture coordinates (u, v)

out vec2 TexCoord;  // Pass texture coordinates to the fragment shader

uniform mat4 model, view, projection;

void main() {
    TexCoord = aTexCoord;  // Pass texture coordinates to the fragment shader

    gl_Position = projection * view * model * vec4(pos, 1);
//    gl_Position = vec4(pos, 1.0);
}
