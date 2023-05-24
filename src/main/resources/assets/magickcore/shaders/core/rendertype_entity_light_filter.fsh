#version 150

#moj_import <fog.glsl>

struct Light
{
    vec4 color;
    vec3 position;
    float radius;
};

uniform sampler2D Sampler4;
uniform sampler2D Sampler5;

uniform vec4 ColorModulator;
uniform vec2 ScreenSize;
uniform mat4 IViewProjMat;

uniform vec4 lights[1000];
uniform int lightCount;
uniform int enableVC;

in vec4 vertexColor;
in float vertexLength;
in vec3 worldCenter;

out vec4 fragColor;

/*
vec4 unpackData(float packed) {
    int r = int(mod(packed * 255.0, 1000000.0) / 10000.0);
    int g = int(mod(packed * 255.0, 10000.0) / 100.0);
    int b = int(mod(packed * 255.0, 100.0));
    int a = int(packed * 255.0);

    float rr = r / 100.0;
    float gg = g / 100.0;
    float bb = b / 100.0;
    float aa = a / 100.0;

    return vec4(rr, gg, bb, aa);
}


*/


vec4 unpackData(int pack) {
    int r = pack / 1000000;
    int g = (pack / 10000) % 100;
    int b = (pack / 100) % 100;
    int a = pack % 100;

    return vec4(float(r) / 100.0, float(g) / 100.0, float(b) / 100.0, float(a));
}

Light getLight(vec4 data) {
    vec4 colorSize = unpackData(int(data.a));
    Light light = Light(
        vec4(colorSize.rgb, 1.0),
        data.xyz,
        colorSize.a
    );
    return light;
}

vec3 rgb2hsv(vec3 c)
{
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));

    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;
    return vec3(abs(q.z + (q.w - q.y) / (6. * d + e)), d / (q.x + e), q.x);
}

vec3 hsv2rgb(vec3 c)
{
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

vec4 boost(vec3 color) {
    vec3 hsv = rgb2hsv(color.rgb);
    float alpha = max(hsv.y, 0.1);

    return vec4(hsv2rgb(vec3(hsv.x, 1.0, hsv.z)), alpha);
}

void main() {
    vec2 screenUV = gl_FragCoord.xy/ScreenSize;
    vec4 positionWorld = IViewProjMat * vec4((screenUV * 2.0 - 1.0), texture(Sampler4, screenUV).r * 2.0f - 1.0f, 1.0);
    vec3 position = positionWorld.xyz / positionWorld.w;
    vec4 worldColor = texture(Sampler5, screenUV);

    float sumR = 0;
    float sumG = 0;
    float sumB = 0;
    float maxIntens = 0;
    float totalIntens = 0;
    int does = 0;
    for(int i = 0; i < lightCount; i++) {
        Light l = getLight(lights[i]);
        float dis = distance(l.position, position);
        float radius = l.radius;
        if(dis < radius) {
            does = 1;
            float intensity = 1.0 - (dis / radius);
            totalIntens += intensity;
            maxIntens = max(maxIntens, intensity);
        }
    }
    if(does < 1)
        discard;

    for(int i = 0; i < lightCount; i++) {
        Light l = getLight(lights[i]);
        float dis = distance(l.position, position);
        float radius = l.radius;
        if(dis < radius) {
            float intensity = 1.0 - (dis / radius);
            sumR += l.color.r * (intensity / totalIntens);
            sumG += l.color.g * (intensity / totalIntens);
            sumB += l.color.b * (intensity / totalIntens);
        }
    }
    vec3 lightsColor = vec3(max(sumR, 0.0f), max(sumG, 0.0f), max(sumB, 0.0f));
    float worldBrightness = max(max(worldColor.r, worldColor.g), worldColor.b);
    float lightingBrightness = max(max(lightsColor.r, lightsColor.g), lightsColor.b);
    vec4 boosrColor = boost(lightsColor);
    fragColor = vec4(boosrColor.rgb, pow(maxIntens, 1.4)*worldBrightness*boosrColor.a);
}