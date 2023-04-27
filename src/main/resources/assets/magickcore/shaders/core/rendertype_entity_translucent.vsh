#version 150

#moj_import <light.glsl>
#moj_import <fog.glsl>

in vec3 Position;
in vec4 Color;

uniform sampler2D Sampler1;
uniform sampler2D Sampler2;

uniform mat4 ViewMat;
uniform mat4 ModelMat;
uniform mat4 ProjMat;
uniform vec4 PosScale;

out vec4 vertexColor;

void main() {
    gl_Position = ProjMat * ViewMat * ModelMat * vec4(Position, 1.0);
    vertexColor = Color;
}