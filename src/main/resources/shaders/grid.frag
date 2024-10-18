#version 330 core

out vec4 FragColor;

in vec3 worldPos; // Interpolated world position from vertex shader

uniform vec3 cameraPos;  // Camera position in world space

void main() {
        // Define grid spacing (fixed in world space, not screen space)
        float gridSpacing = 100.0;  // Set the grid spacing in world units

        // Offset the world position by the camera's position
        vec3 offsetPos = worldPos - cameraPos;

        // Compute the modulus of the world position with the grid spacing
        vec2 gridPos = mod(offsetPos.xy, gridSpacing);

        // Use a small threshold to detect if we're close to a grid line
        float lineThickness = 3;  // The thickness of the grid lines

        // If the fragment is within the thickness range, draw a grid line
        if (gridPos.x < lineThickness || gridPos.y < lineThickness) {
                FragColor = vec4(0.3, 0.3, 0.3, 0.1);  // Grid line color (opaque gray)
        } else {
                FragColor = vec4(1.0, 0.5, 0.2, 0.0);  // Transparent elsewhere
        }
}
