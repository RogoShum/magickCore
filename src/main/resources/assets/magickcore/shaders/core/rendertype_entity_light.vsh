#version 150

#moj_import <light.glsl>
#moj_import <fog.glsl>

in vec3 Position;
in vec4 Color;
in vec4 UV0;
in vec3 Normal;

uniform mat4 ProjMat;
uniform mat4 ViewMat;

out float vertexLength;
out vec3 worldCenter;
out vec4 vertexColor;

void main() {
    gl_Position = ProjMat * ViewMat * vec4(Position, 1.0);
    worldCenter = Position - Normal*UV0.x*100.0;
    vertexLength = UV0.y*100.0;
    vertexColor = Color;
}