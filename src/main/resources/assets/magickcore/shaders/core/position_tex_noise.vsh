#version 150

in vec3 Position;
in vec2 UV0;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform mat4 TextureMat;

out vec2 texCoord0;
out vec2 noiseUV;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    texCoord0 = UV0;
    noiseUV = (TextureMat * vec4(UV0, 0.0, 1.0)).xy;
}
