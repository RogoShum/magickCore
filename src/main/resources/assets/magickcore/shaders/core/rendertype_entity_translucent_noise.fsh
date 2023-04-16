#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;

in float vertexDistance;
in vec4 vertexColor;
in vec4 lightMapColor;
in vec4 overlayColor;
in vec2 texCoord0;
in vec4 normal;
in vec2 noiseUV;
in float gameTime;

out vec4 fragColor;

vec3 random_perlin( vec3 p ) {
    p = vec3(
    dot(p,vec3(127.1,311.7,69.5)),
    dot(p,vec3(269.5,183.3,132.7)),
    dot(p,vec3(247.3,108.5,96.5))
    );
    return -1.0 + 2.0*fract(sin(p)*43758.5453123);
}

float noise_perlin (vec3 p) {
    vec3 i = floor(p);
    vec3 s = fract(p);

    // 3D网格有8个顶点
    float a = dot(random_perlin(i),s);
    float b = dot(random_perlin(i + vec3(1, 0, 0)),s - vec3(1, 0, 0));
    float c = dot(random_perlin(i + vec3(0, 1, 0)),s - vec3(0, 1, 0));
    float d = dot(random_perlin(i + vec3(0, 0, 1)),s - vec3(0, 0, 1));
    float e = dot(random_perlin(i + vec3(1, 1, 0)),s - vec3(1, 1, 0));
    float f = dot(random_perlin(i + vec3(1, 0, 1)),s - vec3(1, 0, 1));
    float g = dot(random_perlin(i + vec3(0, 1, 1)),s - vec3(0, 1, 1));
    float h = dot(random_perlin(i + vec3(1, 1, 1)),s - vec3(1, 1, 1));

    // Smooth Interpolation
    vec3 u = smoothstep(0.,1.,s);

    // 根据八个顶点进行插值
    return mix(mix(mix( a, b, u.x),
    mix( c, e, u.x), u.y),
    mix(mix( d, f, u.x),
    mix( g, h, u.x), u.y), u.z);
}

float noise_turbulence(vec3 p)
{
    float f = 0.0;
    float a = 1.;
    p = 4.0 * p;
    for (int i = 0; i < 5; i++) {
        f += a * abs(noise_perlin(p));
        p = 2.0 * p;
        a /= 2.;
    }
    return f;
}

vec3 calibration( vec3 color ) {
    float r = clamp(color.r, 0.0, 1.0);
    float g = clamp(color.g, 0.0, 1.0);
    float b = clamp(color.b, 0.0, 1.0);
    return vec3(r, g, b);
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

void main() {
    vec4 color = texture(Sampler0, texCoord0);
    color *= vertexColor * ColorModulator;
    color.rgb = mix(overlayColor.rgb, color.rgb, overlayColor.a);
    color *= lightMapColor;
    float fade = linear_fog_fade(vertexDistance, FogStart, FogEnd);

    float c1 = noise_turbulence(vec3(noiseUV, gameTime*50.0));
    vec4 noise = vec4( 1.5*c1, 1.5*c1*c1*c1, c1*c1*c1*c1*c1*c1, 1.0 );
    float alpha = 1.0-rgb2hsv(calibration(noise.rgb)).z;
    fragColor = vec4(color.rgb * fade, color.a * alpha);
}
