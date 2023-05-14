#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler4;
uniform sampler2D Sampler5;

uniform vec4 ColorModulator;
uniform vec2 ScreenSize;
uniform mat4 IViewProjMat;

in vec4 vertexColor;
in float vertexLength;
in vec3 worldCenter;

out vec4 fragColor;

void main() {
    vec4 color = vertexColor * ColorModulator;
    vec2 screenUV = gl_FragCoord.xy/ScreenSize;
    vec4 positionWorld = IViewProjMat * vec4((screenUV * 2.0 - 1.0), texture(Sampler4, screenUV).r * 2.0f - 1.0f, 1.0);
    vec3 worldPosition = positionWorld.xyz / positionWorld.w;

    float dis = distance(worldCenter, worldPosition);
    if(dis < vertexLength) {
        vec4 worldColor = texture(Sampler5, screenUV);
        fragColor = vec4(color.rgb*worldColor.rgb, color.a * pow((1.0 - (dis/vertexLength)), 2));
    } else {
        discard;
    }
}