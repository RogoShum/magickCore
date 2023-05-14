#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;
uniform sampler2D Sampler4;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec2 ScreenSize;

in float vertexDistance;
in vec4 vertexColor;
in vec4 lightMapColor;
in vec4 overlayColor;
in vec2 texCoord0;

out vec4 fragColor;

float near = 0.1;
float far  = 1000.0;

float LinearizeDepth(float depth)
{
    float z = depth * 2.0 - 1.0;
    return (near * far) / (far + near - z * (far - near));
}

vec4 calibration( vec4 color ) {
    float r = clamp(color.r, 0.0, 1.0);
    float g = clamp(color.g, 0.0, 1.0);
    float b = clamp(color.b, 0.0, 1.0);
    float a = clamp(color.a, 0.0, 1.0);
    return vec4(r, g, b, a);
}

void main() {
    vec4 color = texture(Sampler0, texCoord0);
    float depth = texture(Sampler4, vec2(gl_FragCoord.x/ScreenSize.x, gl_FragCoord.y/ScreenSize.y)).r;
    color *= vertexColor * ColorModulator;
    color.rgb = mix(overlayColor.rgb, color.rgb, overlayColor.a);
    color *= lightMapColor;
    float scale = 1.0;
    float dis = abs(LinearizeDepth(depth) - LinearizeDepth(gl_FragCoord.z));
    if(dis <= 1.0) {
        dis = pow(1.0 - dis, 5);
        scale = 1.0+dis*3;
    }
    color *= scale;
    float fade = linear_fog_fade(vertexDistance, FogStart, FogEnd);
    fragColor = vec4(color.rgb * fade, color.a);
}
