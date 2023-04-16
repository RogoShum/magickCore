#version 150

uniform sampler2D DiffuseSampler;

in vec2 texCoord;
in vec2 oneTexel;

uniform vec2 InSize;

uniform float Resolution;
uniform float Saturation;
uniform float MosaicSize;

out vec4 fragColor;

void main() {
    vec2 mosaicInSize = InSize / MosaicSize;
    vec2 fractPix = fract(texCoord * mosaicInSize) / mosaicInSize;

    vec4 baseTexel = texture(DiffuseSampler, texCoord - fractPix);

    vec3 fractTexel = baseTexel.rgb - fract(baseTexel.rgb * Resolution) / Resolution;
    fragColor = vec4(fractTexel.rgb, baseTexel.a);
}
