#version 120

varying vec3 position;
varying vec4 lcolor;
varying float intens;
varying float dist2Obj;

uniform vec3 worldTint;
uniform float worldTintIntensity;
uniform float saturation;

uniform sampler2D sampler;
uniform sampler2D lightmap;
uniform int colMix;
uniform int vanillaTracing;
uniform float fogIntensity;

float luma(vec3 color)
{
    return dot(color, vec3(0.299f, 0.587f, 0.114f));
}

float distSq(vec3 a, vec3 b)
{
    return pow(a.x - b.x, 2) + pow(a.y - b.y, 2) + pow(a.z - b.z, 2);
}

void main()
{
    vec3 lightdark = texture2D(lightmap, gl_TexCoord[2].st).rgb;
    lightdark = clamp(lightdark, 0.0f, 1.0f);
    vec3 lcolor_2 = clamp(lcolor.rgb * intens, 0.0f, 1.0f);
    if (vanillaTracing == 1) lcolor_2 = lcolor_2 * pow(luma(lightdark), 2);

    if (colMix == 1) lightdark = lightdark + lcolor_2;//More washed-out, but more physically correct
    else if(intens > 1.0) {
        lightdark = lcolor_2;
    }

    vec4 baseColor = gl_Color * texture2D(sampler, gl_TexCoord[0].st);
    baseColor = baseColor * vec4(mix(vec3(1.0f), worldTint, worldTintIntensity), 1.0f);

    baseColor = baseColor * vec4(lightdark, 1.0f);
    //vec3 dv = position - playerPos;

    float dist = max(dist2Obj - gl_Fog.start, 0.0f) / (gl_Fog.end - gl_Fog.start);
    float fog = gl_Fog.density * dist * fogIntensity;
    fog = 1.0f - clamp(fog, 0.0f, 1.0f);
    baseColor = vec4(mix(vec3(gl_Fog.color), baseColor.xyz, fog).rgb, baseColor.a);

    //vec3 hsv = rgb2hsv(baseColor.rgb);
    //hsv.y *= saturation;
    gl_FragColor = baseColor;//vec4(hsv2rgb(hsv), baseColor.a);
    //gl_FragColor = texture2D(lightmap, gl_TexCoord[1].st);
}
