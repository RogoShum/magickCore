#version 150

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in vec2 UV2;

uniform sampler2D Sampler3;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform mat4 TextureMat;
uniform float GameTime;
uniform float FogStart;

out vec4 vertexColor;
out vec2 texCoord0;
out vec2 texCoord2;
out vec2 noiseUV;

void main() {
    noiseUV = (TextureMat * vec4(UV0, 0.0, 1.0)).xy;
    vec2 distUV = vec2(noiseUV.x + GameTime * 2.0 + float((int(floor(Position.x)) % 10)) * 0.2, noiseUV.y + GameTime * 2.0 + float((int(floor(Position.z)) % 10)) * 0.2);

    vec3 offsetVec = texture(Sampler3, distUV).rgb;
    offsetVec = ((offsetVec * 2.0) - 1.0) * FogStart;
    vec3 position = Position + vec3(offsetVec.xy, 0.0);
    gl_Position = ProjMat * ModelViewMat * vec4(position, 1.0);

    vertexColor = Color;
    texCoord0 = UV0;
    texCoord2 = UV2;
}