#version 330

layout (location=0) in vec3 Position;
layout (location=1) in vec2 UV0;

layout (location=2) in float Shake;
layout (location=3) in vec2 Scale;
layout (location=4) in vec4 UV1;
layout (location=5) in vec4 Color;
layout (location=6) in vec3 Offset;

uniform sampler2D Sampler3;

uniform mat4 ProjMat;
uniform mat4 TextureMat;
uniform mat4 ViewMat;
uniform float GameTime;
uniform vec4 CameraOrientation;

out vec4 vertexColor;
out vec2 texCoord0;
out vec2 noiseUV;


float getU() {
    float sub = UV1.y - UV1.x;
    return UV1.x + sub * UV0.x;
}

float getV() {
    float sub = UV1.w - UV1.z;
    return UV1.z + sub * UV0.y;
}

vec3 rotateVector( vec4 quat, vec3 vec ) {
    return vec + 2.0 * cross( cross( vec, quat.xyz ) + quat.w * vec, quat.xyz );
}

void main() {
    vec2 trueUV = vec2(getU(), getV());
    noiseUV = (TextureMat * vec4(trueUV, 0.0, 1.0)).xy;
    vec2 distUV = vec2(noiseUV.x + GameTime * 2.0 + float((int(floor(Offset.x)) % 10)) * 0.2, noiseUV.y + GameTime * 2.0 + float((int(floor(Offset.z)) % 10)) * 0.2);

    vec3 offsetVec = texture(Sampler3, distUV).rgb;
    offsetVec = ((offsetVec * 2.0) - 1.0) * Shake;
    vec3 position = rotateVector(CameraOrientation, Position) + vec3(offsetVec.xy, 0.0);
    position.xz *= Scale.x;
    position.y *= Scale.y;

    gl_Position = ProjMat * ViewMat * vec4(position+Offset, 1.0);

    vertexColor = Color;
    texCoord0 = trueUV;
}
