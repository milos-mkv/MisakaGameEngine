#version 330 core

out vec4 FragColor;

in vec2 TexCoord;  // Pass texture coordinates to the fragment shader

uniform sampler2D uTexture;

void main() {
//    FragColor = vec4(1.0f, 0.5f, 0.2f, 1.0f);
    FragColor = texture(uTexture, TexCoord);  // Sample the texture using texture coordinates

}