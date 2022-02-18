#version 110

uniform sampler2D DiffuseSampler;

varying vec2 texCoord;
varying vec2 oneTexel;

uniform vec2 InSize;

uniform vec2 BlurDir;
uniform float Radius;

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

float max_value( vec3 color ) {
    float alpha = 0.0;
    if ( color.r > alpha ) {
        alpha = color.r;
    }

    if ( color.g > alpha ) {
        alpha = color.g;
    }

    if ( color.b > alpha ) {
        alpha = color.b;
    }

    return alpha * alpha;
}

void main() {
    vec4 blurred = vec4(0.0);
    for(float r = -Radius; r <= Radius; r += 1.0) {
        vec4 sampleValue = texture2D(DiffuseSampler, texCoord + oneTexel * r * BlurDir);
        blurred = blurred + sampleValue;
    }

    vec3 colour = blurred.rgb / (Radius * 2.0 + 1.0);
    gl_FragColor = vec4(colour.rgb, max_value(colour.rgb));
}
