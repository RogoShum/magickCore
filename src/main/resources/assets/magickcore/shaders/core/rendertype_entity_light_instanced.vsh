#version 330

#moj_import <light.glsl>
#moj_import <fog.glsl>

layout (location=0) in vec3 Normal;

layout (location=1) in float NormalScale;
layout (location=2) in float Size;
layout (location=3) in vec4 Color;
layout (location=4) in vec3 Pos;

uniform mat4 ProjMat;
uniform mat4 ViewMat;

out float vertexLength;
out vec3 worldCenter;
out vec4 vertexColor;

void main() {
    gl_Position = ProjMat * ViewMat * vec4(Normal*NormalScale+Pos, 1.0);
    worldCenter = Pos;
    vertexLength = Size;
    vertexColor = Color;
}