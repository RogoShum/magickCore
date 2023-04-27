#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler4;
uniform sampler2D Sampler5;

uniform vec4 ColorModulator;
uniform vec2 ScreenSize;
uniform vec4 PosScale;
uniform mat4 IViewProjMat;
uniform mat4 IModelMat;

in vec4 vertexColor;

out vec4 fragColor;

void main() {
    vec4 color = vertexColor * ColorModulator;
    vec2 screenUV = gl_FragCoord.xy/ScreenSize;
    vec4 positionWorld = IViewProjMat * vec4((screenUV * 2.0 - 1.0), texture(Sampler4, screenUV).r * 2.0f - 1.0f, 1.0);
    vec3 worldPosition = positionWorld.xyz / positionWorld.w;
    vec4 localPosition = IModelMat * vec4(worldPosition, 1.0);

    float dis = distance(localPosition.xyz, vec3(0.0));
    if(dis < PosScale.w) {
        vec4 worldColor = vec4(1.0);
        if(PosScale.x > 1)
            worldColor = texture(Sampler5, screenUV);
        fragColor = vec4(color.rgb*worldColor.rgb, color.a * pow((1.0 - (dis/PosScale.w)), 2));
    } else {
        discard;
    }
}