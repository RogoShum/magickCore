#version 150

#moj_import <light.glsl>
#moj_import <fog.glsl>

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV1;
in ivec2 UV2;
in vec3 Normal;

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

uniform vec3 Light0_Direction;
uniform vec3 Light1_Direction;

out float vertexDistance;
out vec4 vertexColor;
out vec4 lightMapColor;
out vec4 overlayColor;
out vec2 texCoord0;
out vec4 normal;

void main() {
    texCoord0 = (TextureMat * vec4(UV0, 0.0, 1.0)).xy;
    vec2 distUV = vec2(texCoord0.x + GameTime * 2.0 + float((int(floor(Position.x)) % 10)) * 0.2, texCoord0.y + GameTime * 2.0 + float((int(floor(Position.z)) % 10)) * 0.2);

    vec3 offsetVec = texture(Sampler3, distUV).rgb;
    offsetVec = ((offsetVec * 2.0) - 1.0) * FogColor.a;
    vec3 position = Position + offsetVec;
    gl_Position = ProjMat * ModelViewMat * vec4(position, 1.0);

    vertexDistance = fog_distance(ModelViewMat, IViewRotMat * position, FogShape);
    vertexColor = minecraft_mix_light(vec3(0, 1, 0), vec3(0, -1, 0), Normal, Color);
    lightMapColor = texelFetch(Sampler2, UV2 / 16, 0);
    overlayColor = texelFetch(Sampler1, UV1, 0);
    normal = ProjMat * ModelViewMat * vec4(Normal, 0.0);
}
