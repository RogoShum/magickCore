#version 110

uniform sampler2D DiffuseSampler;

varying vec2 texCoord;
varying vec2 oneTexel;

uniform vec2 InSize;

uniform vec2 BlurDir;
uniform float Radius;
uniform float Alpha;

#define SCREENEDGE 0.01

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

vec3 calibration( vec3 color ) {
    float r = clamp(color.r, 0.0, 1.0);
    float g = clamp(color.g, 0.0, 1.0);
    float b = clamp(color.b, 0.0, 1.0);
    return vec3(r, g, b);
}

vec3 boost(vec3 color, int type) {
    vec3 hsv = rgb2hsv(color.rgb);
    float s = hsv.y;
    float v = 1.0;

    if(type == 0) {
        s *= 5.0;
        if (s > 1.0)
        s = 1.0;
    } else {
        s *= 1.2;
        if (s > 1.0)
        s = 1.0;
    }

    return hsv2rgb(vec3(hsv.x, s, v));
}

void main() {
    vec4 center = texture2D(DiffuseSampler, texCoord);
    vec4 blurred = vec4(0.0);
    float totalStrength = 0.0;
    float totalAlpha = 0.0;
    float totalSamples = 0.0;
    float count = 1.0;
    float countAlpha = 1.0;

    blurred = center;
    totalAlpha = center.a;
    totalSamples = 1.0;
    for(float r = -Radius; r <= Radius; r += 1.0) {
        vec4 sampleValue0 = texture2D(DiffuseSampler, texCoord + oneTexel * r * BlurDir);
        vec4 sampleValue1 = texture2D(DiffuseSampler, texCoord + oneTexel * -r * BlurDir);

        countAlpha += 2.0;
        if (sampleValue0.a <= 0.0) {
            if(center.a > 0.0) {
                count += 1.0;
                blurred += vec4(center.rgb, 0.0);
            }
        } else {
            count += 1.0;
            totalAlpha += sampleValue0.a;
            blurred += vec4(sampleValue0.rgb, 0.0);
        }

        if (sampleValue1.a <= 0.0) {
            if(center.a > 0.0) {
                count += 1.0;
                blurred += vec4(center.rgb, 0.0);
            }
        } else {
            count += 1.0;
            totalAlpha += sampleValue1.a;
            blurred += vec4(sampleValue1.rgb, 0.0);
        }
    }
    float finalAlpha = clamp(totalAlpha / countAlpha * Alpha, 0.0, 1.0);
    gl_FragColor = vec4(blurred.rgb / count, finalAlpha);
}
