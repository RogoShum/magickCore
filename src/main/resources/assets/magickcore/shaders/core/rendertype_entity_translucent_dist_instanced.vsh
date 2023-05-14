#version 330

#moj_import <light.glsl>
#moj_import <fog.glsl>

layout (location=0) in vec3 Position;
layout (location=1) in vec3 Normal;
layout (location=2) in vec2 UV0;
layout (location=3) in float Alpha;

layout (location=4) in vec2 UV2;
layout (location=5) in vec4 UV1;
layout (location=6) in vec4 Color;
layout (location=7) in mat4 ModelMat;

uniform sampler2D Sampler1;
uniform sampler2D Sampler2;
uniform sampler2D Sampler3;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform mat3 IViewRotMat;
uniform mat4 TextureMat;
uniform int FogShape;
uniform float GameTime;
uniform vec4 FogColor;

out float vertexDistance;
out vec4 vertexColor;
out vec4 lightMapColor;
out vec2 texCoord0;
out vec4 overlayColor;

float getU() {
    float sub = UV1.y - UV1.x;
    return UV1.x + sub * UV0.x;
}

float getV() {
    float sub = UV1.w - UV1.z;
    return UV1.z + sub * UV0.y;
}

void main() {
    vec2 fixedUV = vec2(getU(), getV());
    texCoord0 = (TextureMat * vec4(fixedUV, 0.0, 1.0)).xy;
    vec3 worldPosition = ModelMat[3].xyz;
    vec2 distUV = vec2(texCoord0.x + GameTime * 2.0 + float((int(floor(worldPosition.x)) % 10)) * 0.2, texCoord0.y + GameTime * 2.0 + float((int(floor(worldPosition.z)) % 10)) * 0.2);

    vec3 offsetVec = texture(Sampler3, distUV).rgb;
    offsetVec = ((offsetVec * 2.0) - 1.0) * FogColor.a;
    vec3 position = Position + offsetVec;
    gl_Position = ProjMat * ModelViewMat * ModelMat * vec4(position, 1.0);

    vertexDistance = fog_distance(ModelViewMat, IViewRotMat * position, FogShape);
    vertexColor = minecraft_mix_light(vec3(0, 1, 0), vec3(0, -1, 0), Normal, vec4(Color.rgb, Color.a*Alpha));
    lightMapColor = texelFetch(Sampler2, ivec2(UV2) / 16, 0);
    overlayColor = vec4(1.0);
}
