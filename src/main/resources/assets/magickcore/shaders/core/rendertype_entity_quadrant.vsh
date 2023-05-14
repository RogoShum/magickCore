#version 150

#moj_import <light.glsl>
#moj_import <fog.glsl>

in vec3 Position;
in vec2 UV0;
in vec4 Color;
in vec3 Normal;

uniform mat4 ProjMat;
uniform mat4 ViewMat;

out vec2 noiseUV;
out vec3 worldPos;
out vec3 localPos;
out vec2 textureCoord;
out vec4 vColor;
flat out float vertexLength;
out vec3 worldCenter;

void main() {
    gl_Position = ProjMat * ViewMat * vec4(Position, 1.0);
    vColor = Color;
    textureCoord = UV0;
    worldPos = Position;
    localPos = Normal;
    worldCenter = Position - Normal;
    vertexLength = length(Normal);
    noiseUV = UV0+vec2(worldPos.x*0.1, worldPos.z*0.1);
}
