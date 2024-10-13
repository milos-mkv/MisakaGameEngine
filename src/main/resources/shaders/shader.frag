#version 330 core

out vec4 FragColor;

in vec2 TexCoord;  // Pass texture coordinates to the fragment shader

uniform sampler2D uTexture;
uniform bool isCamera;
uniform vec3 bgColor;
void main() {
//    FragColor = vec4(1.0f, 0.5f, 0.2f, 1.0f);
    // Sample the texture using texture coordinates
    if (isCamera) {
        FragColor = vec4(bgColor, 1);
    } else {
        FragColor = texture(uTexture, TexCoord);
    }
}